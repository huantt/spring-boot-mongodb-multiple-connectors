package com.huantt.springbootmongodbmultipleconnectors.repository.first

import com.huantt.springbootmongodbmultipleconnectors.model.FirstModel
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

@CompileStatic
interface FirstRepository extends MongoRepository<ObjectId, FirstModel> {
}
