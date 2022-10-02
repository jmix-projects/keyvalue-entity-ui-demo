package com.company.demo.screen.foo;

import com.company.demo.app.FooService;
import io.jmix.core.ValueLoadContext;
import io.jmix.core.entity.KeyValueEntity;
import io.jmix.ui.model.KeyValueCollectionContainer;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@UiController("Foo.browse")
@UiDescriptor("foo-browse.xml")
@LookupComponent("foosTable")
public class FooBrowse extends StandardLookup<KeyValueEntity> {

    @Autowired
    private KeyValueCollectionContainer foosDc;
    @Autowired
    private FooService fooService;

    @Install(to = "foosDl", target = Target.DATA_LOADER)
    private List<KeyValueEntity> foosDlLoadDelegate(ValueLoadContext valueLoadContext) {
        return fooService.loadList(valueLoadContext.getQuery().getCondition());
    }

    @Install(to = "foosTable.create", subject = "newEntitySupplier")
    private KeyValueEntity foosTableCreateNewEntitySupplier() {
        return foosDc.createEntity();
    }
}