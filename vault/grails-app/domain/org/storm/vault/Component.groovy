package org.storm.vault

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode(includes = ['type', 'name'])
@ToString(includeNames = true, includePackage = false, includes = ['type', 'name', 'desc'])
class Component implements Serializable {
    enum Type {
        API, EJB, DATABASE, OTHER, WEBSERVICE
    }

    String name, desc
    Type type

    static constraints = {
        type required: true, nullable: false
        name required: true, nullable: false, maxSize: 512
        desc required: false, nullable: true, maxSize: 1024
    }
}