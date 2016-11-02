package org.storm.vault

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * Primary domain of Vault and holds the information about a Resource.
 *
 * @author Timothy Storm
 */
@EqualsAndHashCode(includes = 'eai')
@ToString(includeNames = true, includePackage = false, includes = ['eai', 'name', 'detail', 'assets'])
class Resource implements Serializable {
  Long eai
  String name

  static hasMany = [assets:Asset]
  static mappedBy = [assets:'root']
  static hasOne = [detail:Detail]
  static constraints = {
    name required:true, nullable:false, maxSize:64
    assets required:false, nullable:true
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
}
