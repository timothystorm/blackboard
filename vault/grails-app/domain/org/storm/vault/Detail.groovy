package org.storm.vault

import groovy.transform.ToString

@ToString(includeNames = true, includePackage = false, includes = ['desc', 'disposition'])
class Detail implements Serializable {
  Resource resource
  String desc
  Disposition disposition

  static constraints = {
    desc required:false, nullable:true, maxSize:512
    disposition required:true
  }
}