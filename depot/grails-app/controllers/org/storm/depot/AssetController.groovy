package org.storm.depot

class AssetController {
  SessionFactory sessionFactory

  def create() {
    def assets = Asset.list()
    [asset: new Asset(), assets:assets, assetOf:assets]
  }

  def delete() {
    with{asset -> 
      removeAssociations(asset)
      asset.delete() 
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
    upsave(asset)
    redirect action:'show', id:asset.id
  }

  def show() {
    with{asset -> [asset:asset]}
  }

  def update() {
    with{asset ->
      removeAssociations(asset)

      // capture new asset params and save
      upsave((asset.properties = params))
      redirect action:'show', id:asset.id
    }
  }

  /**
   * Utility to update/save an Asset and adding child/parent associations
   */
  private def upsave(Asset asset){
    if(asset.save()) addAssociations(asset)
  }

   /** 
    * Utility finds the specified Asset and if found executes the closure with
    * the found Asset as an arg
    */
  private def with(id='id', Closure closure) {
    def asset = Asset.get(params[id])
    if(asset) closure(asset)
    else {
      flash.message = "Asset not found"
      redirect action:'index'
    }
  }

  private def addAssociations(Asset asset) {
    // add cross reference assets
    asset.assets?.each{it.removeFromAssetOf(asset).save()}
    asset.assetOf?.each{it.removeFromAssets(asset).save()}
    sessionFactory?.getCurrentSession()?.flush()
  }

  private def removeAssociations(Asset asset) {
    // remove cross reference assets
    asset.assets?.each{it.removeFromAssetOf(asset).save()}
    asset.assetOf?.each{it.removeFromAssets(asset).save()}
    sessionFactory?.getCurrentSession()?.flush()
  }
}
