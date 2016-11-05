package org.storm.vault

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode(includes = 'ldap')
@ToString(includeNames = true, includePackage = false, includes = ['ldap', 'name', 'email'])
class Contact implements Serializable {
    Long ldap
    Name name
    String email

    static constraints = {
        email required: true, nullable: false, email: true
        name required: true, nullable: false
    }
    static embedded = ['name']
    static mapping = { id name: 'ldap', generator: 'assigned' }

    def getResources() {
        Resource.withCriteria {
            contacts {
                eq('id', ldap)
            }
        }
    }
}