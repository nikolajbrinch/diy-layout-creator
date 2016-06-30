package org.diylc.core.components.registry;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.UUID;

import org.diylc.core.IDIYComponent;
import org.diylc.core.components.ComponentModel;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Component
public class DefaultComponentFactory implements ComponentFactory {

    @Override
    public IDIYComponent createComponent(ObjectMapper mapper, ObjectNode root, ComponentModel componentModel) {
        IDIYComponent component;

        boolean hasId = false;

        JsonNode idValue = root.findValue("id");

        if (idValue != null) {
            String id = idValue.textValue();

            if (id != null && !id.isEmpty()) {
                hasId = true;
            }
        }

        if (!hasId) {
            root.put("id", createUniqueId());
        }

        try {
            component = mapper.treeToValue(root, componentModel.getInstanceClass());
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }

        component.setComponentModel(componentModel);

        return component;
    }

    @Override
    public IDIYComponent createComponent(ComponentModel componentModel) throws Exception {
        /*
         * Instantiate the component.
         */
        Class<? extends IDIYComponent> instanceClass = componentModel.getInstanceClass();
        IDIYComponent component = null;

        try {
            Constructor<? extends IDIYComponent> constructor = instanceClass.getConstructor(String.class);
            component = constructor.newInstance(createUniqueId());
        } catch (NoSuchMethodException e) {
            component = instanceClass.newInstance();
            Method method = instanceClass.getMethod("setId", String.class);
            method.invoke(component, createUniqueId());
        }

        component.setComponentModel(componentModel);

        return component;
    }

    public static String createUniqueId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public IDIYComponent createComponent(IDIYComponent component) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        IDIYComponent clone = null;

        try {
            objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(this);
            objectOutputStream.close();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            clone = (IDIYComponent) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            clone = component;
        }

        clone.setComponentModel(component.getComponentModel());

        return clone;
    }
}
