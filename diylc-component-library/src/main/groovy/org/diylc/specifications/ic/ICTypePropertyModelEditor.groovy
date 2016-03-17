package org.diylc.specifications.ic

import javax.swing.JComboBox;
import javax.swing.JTextField;

import org.diylc.core.components.properties.DefaultPropertyModelEditor;
import org.diylc.core.components.properties.PropertyModel;

class ICTypePropertyModelEditor extends DefaultPropertyModelEditor<ICTypePropertyModel> {

    ICPinCount customPinCount

    String customValue

    public ICTypePropertyModelEditor() {
        super(ICTypePropertyModel)
    }

    @Override
    public void init() {
        ICSpecificationModel model = (ICSpecificationModel) getPropertyValue()

        getEditor('Value', JTextField).setText(model.value)
        getEditor('Pins', JComboBox.class).setSelectedItem(model.pinCount)
    }

    @Override
    public void customDeselected() {
        setEnabled('Value', true)
        setEnabled('Pins', true)

        customPinCount = (ICPinCount) getValue('Pins')
        customValue = getValue('Value')

        getEditor('Value', JTextField).setText('')
    }

    @Override
    public void customSelected() {
        setEnabled('Value', true)
        setEnabled('Pins', true)

        getEditor('Value', JTextField.class).setText(customValue)
        getEditor('Pins', JComboBox.class).setSelectedItem(customPinCount)

        ICSpecificationModel model = (ICSpecificationModel) getPropertyValue()
        String name = getPropertyDescriptor().name
        getPropertyDescriptor().changed = true

        int pinsPerSide = customPinCount.getValue().intdiv(2) as int

        ICSpecificationModel oldModel = new ICSpecificationModel(model)

        model.pinCount = customPinCount
        model.pinsLeft = (List<Map<String, Object>>) (1..(pinsPerSide)).collect { index -> ['id': index] }
        model.pinsTop = []
        model.pinsRight = ((List<Map<String, Object>>)(1..(pinsPerSide)).collect { index -> ['id': index + pinsPerSide] } ).reverse()
        model.pinsBottom = []
        model.specification = null
        model.value = getValue('Value')

        firePropertyChange(name, oldModel, model)
    }

   
    @Override
    public void itemSelected(ICTypePropertyModel icSpecification) {
        setEnabled('Value', false)
        setEnabled('Pins', false)

        getEditor('Value', JTextField.class).setText(icSpecification.name)

        Map pins = icSpecification.pins

        List left = pins['left']
        List top = pins['top']
        List right = pins['right']
        List bottom = pins['bottom']

        int pinCount = (left.size() + top.size() + right.size() + bottom.size())

        ICSpecificationModel model = (ICSpecificationModel) getPropertyValue()
        String name = getPropertyDescriptor().getName()
        getPropertyDescriptor().setChanged(true)

        ICSpecificationModel oldModel = new ICSpecificationModel(model)

        model.pinCount = ICPinCount.getPinCount(pinCount)
        model.pinsLeft = left
        model.pinsTop = top
        model.pinsRight = right
        model.pinsBottom = bottom
        model.specification = icSpecification
        model.value = icSpecification.name

        getEditor('Pins', JComboBox.class).setSelectedItem(model.pinCount)

        firePropertyChange(name, oldModel, model)
    }

    @Override
    public PropertyModel getSelectedSpecification() {
        ICSpecificationModel model = (ICSpecificationModel) getPropertyValue()

        PropertyModel specification
        
        if (model == null || model.specification == null) {
            specification = getPropertyDescriptor().propertyModels.get(0)
        } else {
            specification = model.specification
        }
        
        return specification
    }

}
