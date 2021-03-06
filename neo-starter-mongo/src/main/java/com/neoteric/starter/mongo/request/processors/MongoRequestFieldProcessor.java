package com.neoteric.starter.mongo.request.processors;

import com.google.common.collect.Lists;
import com.neoteric.starter.mongo.request.FieldMapper;
import com.neoteric.starter.mongo.request.processors.fields.MongoFieldSubProcessor;
import com.neoteric.starter.mongo.request.processors.fields.MongoFieldToLogicalOperatorSubProcessor;
import com.neoteric.starter.mongo.request.processors.fields.MongoFieldToOperatorSubProcessor;
import com.neoteric.starter.request.RequestField;
import com.neoteric.starter.request.RequestObject;
import com.neoteric.starter.request.RequestObjectType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class MongoRequestFieldProcessor implements MongoRequestObjectProcessor<RequestField> {

    @Autowired
    private MongoFieldToOperatorSubProcessor mongoFieldToOperatorSubProcessor;

    @Autowired
    private MongoFieldToLogicalOperatorSubProcessor mongoFieldToLogicalOperatorSubProcessor;

    @Override
    public Boolean apply(RequestObjectType key) {
        return RequestObjectType.FIELD.equals(key);
    }

    @Override
    public List<Criteria> build(RequestField field, Map<RequestObject, Object> fieldValues, FieldMapper fieldMapper) {
        List<Criteria> allFieldCriteria = Lists.newArrayList();

        fieldValues.forEach((requestObject, operatorValue) -> {
            Criteria criteria = Stream.<MongoFieldSubProcessor>of(mongoFieldToOperatorSubProcessor, mongoFieldToLogicalOperatorSubProcessor)
                    .filter(mongoFieldSubProcessor -> mongoFieldSubProcessor.apply(requestObject.getType()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(requestObject.getType() + " cannot be applied to Field."))
                    .build(field, requestObject, operatorValue, fieldMapper);
            allFieldCriteria.add(criteria);
        });

        return allFieldCriteria;
    }
}
