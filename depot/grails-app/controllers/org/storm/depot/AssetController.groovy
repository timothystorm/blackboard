package org.storm.depot

class AssetController {
  def sessionFactory

  def create() {
    def assets = Asset.list()
    [asset: new Asset(), assets:assets, assetOf:assets]
  }

  def delete() {
    with{asset -> 
      removeAsset(asset)
      asset.delete(flush:true) 
      redirect action:'index'
    }
  }


  def edit() {
    with{asset -> 
      def assets = Asset.list().findAll{it != asset}
      [asset:asset, assets:assets, assetOf:assets]
    }
  }

  def index() {
    [assets: Asset.list(params)]
  }

  def save() {
    def asset = new Asset(params)
    asset.save()
    addAsset(asset)
    redirect action:'show', id:asset.id
  }

  def show() {
    with{asset -> [asset:asset]}
  }

  def update() {
    with{asset ->
      removeAsset(asset)
      asset.properties = params
      asset.save(flush:true) 
      addAsset(asset)
      redirect action:'show', id:asset.id
    }
  }

  /**
   * Add assets xref
   */
  private def addAsset(Asset asset) {
    iter(asset.assets, {it.addToAssetOf(asset).save()})
    iter(asset.assetOf, {it.addToAssets(asset).save()})
    sessionFactory.getCurrentSession().flush()
  }

  /** 
   * Remove assets xref 
   */
  private def removeAsset(Asset asset) {
    iter(asset.assets, {it.removeFromAssetOf(asset).save()})
    iter(asset.assetOf, {it.removeFromAssets(asset).save()})
    sessionFactory.getCurrentSession().flush()
  }

  /** 
   * Utility finds the specified Asset and if found executes the closure with
   * the found Asset as an arg
   */
  private def with(Closure closure) {
    def asset = Asset.get(params['id'])
    if(asset) closure(asset)
    else {
      flash.message = "Asset not found"
      redirect action:'index'
    }
  }

  /**
   * thread save iteration of list which uses a
   * traditional iterator instead of a functional
   * iterator to prevent concurrent modifications
   * errors
   */
  private def iter(def list, Closure closure) {
    if(list) {
      def arr = []
      if(arr.addAll(list)) arr.each{closure(it)}
    }
  }
}
