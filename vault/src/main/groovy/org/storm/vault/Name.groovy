package org.storm.vault

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * Person name
 *
 * @author Timothy Storm
 */
@EqualsAndHashCode(includes = ['given', 'family', 'middle'])
@ToString(includeNames = true, includePackage = false, includes = ['given', 'family', 'middle'])
class Name {
    String given, family
    String[] middle

    static constraints = {
        family required: true, nullable: false, maxSize: 64
        given required: true, nullable: false, maxSize: 64
        middle required: false, nullable: true
    }
}