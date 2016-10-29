package org.storm.depot

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode(includes = ['eai'])
class Asset {
    Integer eai
    String name
    String desc

    static hasMany = [assets:Asset, assetOf:Asset]
    static mappedBy = [assets:'assetOf', assetOf:'assets']
    static mapping = {
        assets fetch: 'join'
        assetOf fetch: 'join'
    }

    static constraints = {
        eai nullable:false, required:true, unique:true, min:0
        name nullable:false, required:true, maxSize:64
        desc nullable:true, required:false, maxSize:512
        assets nullable:true
        assetOf nullable:true
    }
}
