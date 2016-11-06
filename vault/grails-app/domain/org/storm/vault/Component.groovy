package org.storm.vault

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode(includes = ['type', 'name'])
@ToString(includeNames = true, includePackage = false, includes = ['type', 'name', 'detail'])
class Component {
    enum Type {
        API, DATABASE, EJB, MODULE, OTHER, RMI, WEBSERVICE
    }

    String name, detail
    Type type

    static constraints = {
        name required: true, nullable: false, maxSize: 64
        type required: true, nullable: false
        detail required: false, nullable: true, maxSize: 512
    }

    def componentOf() {
        Resource.withCriteria {
            components {
                eq('id', id)
            }
        }
    }
}
