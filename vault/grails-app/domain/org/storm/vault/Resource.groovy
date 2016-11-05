package org.storm.vault

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * Primary domain of Vault and holds the information about a Resource.
 *
 * @author Timothy Storm
 */
@EqualsAndHashCode(includes = 'eai')
@ToString(includeNames = true, includePackage = false, includes = ['eai', 'name', 'desc', 'contacts', 'assets'])
class Resource implements Serializable {
    BigInteger eai
    String name, desc

    static hasMany = [assets: Asset, contacts: Contact, components: Component]
    static mappedBy = [assets: 'root']
    static constraints = {
        assets required: false, nullable: true
        contacts required: false, nullable: true
        components required: false, nullable: true
        desc required: false, nullable: true, maxSize: 512
        name required: true, nullable: false, maxSize: 64
    }
    static mapping = { id name: 'eai', generator: 'assigned' }

    /**
     * @return Resources this is an asset of
     */
    def getAssetOf() {
        Asset.findAllByAsset(this)
    }

    /**
     * clears assets and contacts
     */
    def clear() {
        Asset.removeRoot(this)
        contacts?.clear()
    }

    /**
     * Adds a resource as an asset to this
     *
     * @param resource - to be added as an asset
     * @return this Resource for further configuration
     */
    def addToAssets(Resource resource) {
        addToAssets(new Asset(root: this, asset: resource))
    }

    /**
     * removes this Resource as a root/asset
     */
    def beforeDelete() {
        Asset.remove(this)
    }
}
