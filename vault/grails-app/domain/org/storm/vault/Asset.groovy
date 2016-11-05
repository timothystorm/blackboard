package org.storm.vault

import groovy.transform.EqualsAndHashCode

/**
 * XREF of Resource dependencies.  Should not be used directly but
 * only throught the Resource domain
 *
 * @author Timothy Storm
 */
@EqualsAndHashCode(includes = 'root, asset')
class Asset implements Serializable {
    Resource asset
    static belongsTo = [root: Resource]
    static mapping = {
        version false
        id composite: ['root', 'asset']
    }

    /**
     * removes all resource from all asset associations
     *
     * @param resource - to be deleted
     */
    static def remove(Resource resource) {
        executeUpdate("DELETE FROM Asset WHERE root=:resource OR asset=:resource", [resource: resource])
    }

    /**
     * removes the root resource and all dependant assets
     *
     * @param resource - to be deleted
     */
    static def removeRoot(Resource resource) {
        executeUpdate("DELETE FROM Asset WHERE root=:resource", [resource: resource])
    }
}
