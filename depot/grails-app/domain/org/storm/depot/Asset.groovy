package org.storm.depot

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode(includes = ['eai'])
class Asset {
    BigInteger eai
    String name
    String desc

    static hasMany = [assets:Asset, assetOf:Asset]
    static mappedBy = [assets:'assetOf', assetOf:'assets']
    static mapping = {
        id generator: 'assigned', name: 'eai'
        assets fetch: 'join'
        assetOf fetch: 'join'
    }

    static constraints = {
        name nullable:false, required:true, maxSize:64
        desc nullable:true, required:false, maxSize:512
        assets nullable:true
        assetOf nullable:true
    }
}
