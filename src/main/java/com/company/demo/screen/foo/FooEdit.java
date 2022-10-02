package com.company.demo.screen.foo;

import com.company.demo.app.FooService;
import io.jmix.core.SaveContext;
import io.jmix.core.ValueLoadContext;
import io.jmix.core.entity.KeyValueEntity;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Set;

@UiController("Foo.edit")
@UiDescriptor("foo-edit.xml")
@EditedEntityContainer("fooDc")
public class FooEdit extends StandardEditor<KeyValueEntity> {

    @Autowired
    private FooService fooService;

    @Install(to = "fooDl", target = Target.DATA_LOADER)
    private KeyValueEntity fooDlLoadDelegate(ValueLoadContext valueLoadContext) {
        KeyValueEntity entity = getEditedEntity();
        if (entity.getId() == null)
            return entity;
        return fooService.load((Long) entity.getId());
    }

    @Install(target = Target.DATA_CONTEXT)
    private Set<Object> commitDelegate(SaveContext saveContext) {
        KeyValueEntity keyValueEntity = getEditedEntity();
        KeyValueEntity savedEntity = fooService.save(keyValueEntity);
        return Collections.singleton(savedEntity);
    }
}