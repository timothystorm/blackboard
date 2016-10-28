package org.storm.depot

class AssetController {
   def create() {
     def assets = Asset.list()
     [asset: new Asset(), assets:assets, assetOf:assets]
   }

   def delete() {
     with{asset -> 
       removeAssociations(asset)
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
     upsave(asset)
     redirect action:'show', id:asset.id
   }

   def show() {
     with{asset -> [asset:asset]}
   }

   def update() {
     with{asset ->
       removeAssociations(asset)

       asset.properties = params
       upsave(asset)
       redirect action:'show', id:asset.id
     }
   }

   /**
    * Utility to update/save an Asset and adding child/parent associations
    */
   private def upsave(Asset asset){
     if(asset?.save(flush:true)) {
       addAssociations(asset)
     }
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

   private def addAssociations(Asset asset){
     // need to move the assets to tmp to prevent concurrent modification exceptions
     def tmp = []
     tmp.addAll(asset.assets)
     tmp.each{it.addToAssetOf(asset).save(flush:true)}

     tmp = []
     tmp.addAll(asset.assetOf)
     tmp.each{it.addToAssets(asset).save(flush:true)}
   }

   private def removeAssociations(Asset asset){
     // need to move the assets to tmp to prevent concurrent modification exceptions
     def tmp = [] 
     tmp.addAll(asset.assets)
     tmp.each{it.removeFromAssetOf(asset).save(flush:true)}

     tmp = []
     tmp.addAll(asset.assetOf)
     tmp.each{it.removeFromAssets(asset).save(flush:true)}
   }
}
