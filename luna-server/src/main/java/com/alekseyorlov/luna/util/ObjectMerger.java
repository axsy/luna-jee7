package com.alekseyorlov.luna.util;

import java.util.Collection;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.*;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Component;

@Component
public class ObjectMerger {

    public enum MergeStartegy {

        /**
         * Applies nulls
         */
        UPDATE,

        /**
         * Skips nulls
         */
        PATCH
    }

    @Autowired
    private Repositories repositories;

    public <Type> Type merge(Type target, Type source, MergeStartegy mergeStartegy) {
        BeanWrapper targetWrapper = new BeanWrapperImpl(target);
        BeanWrapper sourceWrapper = new BeanWrapperImpl(source);
        PersistentEntity<?, ?> entity = repositories.getPersistentEntity(target.getClass());

        entity.doWithProperties(new SimplePropertyHandler() {

            @Override
            public void doWithPersistentProperty(PersistentProperty<?> property) {
                Object sourceVal = sourceWrapper.getPropertyValue(property.getName());
                if (!property.isIdProperty() && ((mergeStartegy == MergeStartegy.PATCH && sourceVal != null)
                        || mergeStartegy == MergeStartegy.UPDATE)) {
                    targetWrapper.setPropertyValue(property.getName(), sourceVal);
                }
            }
        });

        entity.doWithAssociations(new SimpleAssociationHandler() {

            @Override
            public void doWithAssociation(Association<? extends PersistentProperty<?>> association) {
                PersistentProperty<?> property = association.getInverse();
                Object sourceVal = sourceWrapper.getPropertyValue(property.getName());

                if (((mergeStartegy == MergeStartegy.PATCH && sourceVal != null)
                    || mergeStartegy == MergeStartegy.UPDATE)) {
                    if (property.getType().equals(Collection.class)) {
                        Collection targetCollection = (Collection) targetWrapper.getPropertyValue(property.getName());
                        targetCollection.clear();
                        targetCollection.addAll((Collection) sourceWrapper.getPropertyValue(property.getName()));
                    } else {
                        targetWrapper.setPropertyValue(property.getName(), sourceVal);
                    }
                }
            }
        });

        return target;
    }
}
