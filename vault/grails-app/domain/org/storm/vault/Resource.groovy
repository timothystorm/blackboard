package org.storm.vault

import groovy.transform.EqualsAndHashCode

/**
 * Primary domain of Vault and hold the identity information about a
 * Resource.
 *
 * @author Timothy Storm
 */
@EqualsAndHashCode(includes = 'eai')
class Resource implements Serializable {
  Long eai
  String name

  static hasMany = [assets:Asset]
  static mappedBy = [assets:'root']
  static constraints = {
    name nullable:false, required:true, maxSize:64
  }
  static mapping = {
    id name:'eai', generator:'assigned'
  }

  /**
   * @return Resources this is an asset of
   */
  def getAssetOf() {
    Asset.findAllByAsset(this)
  }

  /**
   * clears assets 
   */
  def clear() {
    assets?.clear()
    Asset.removeRoot(this)
    return this
  }

  /** 
   * Adds a resource as an asset to this
   *
   * @param resource - to be added as an asset
   * @return this Resource for further configuration
   */
  def addToAssets(Resource resource) {
    addToAssets(new Asset(root:this, asset:resource))
  }

  /**
   * removes this Resource from Asset 
   */ 
  def beforeDelete() {
    Asset.remove(this)
  }

  String toString() {
    "Resource(eai:${eai}, name:${name}, assets:${assets})"
  }
}
