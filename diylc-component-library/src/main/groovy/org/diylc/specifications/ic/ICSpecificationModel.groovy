package org.diylc.specifications.ic

import org.diylc.core.annotations.EditableProperty
import org.diylc.specifications.SpecificationModel


class ICSpecificationModel implements Serializable {

    @EditableProperty(name = 'Model')
    ICSpecification specification = null

    @EditableProperty(name = 'Pins')
    ICPinCount pinCount = ICPinCount._14

    @EditableProperty
    String value

    List<Map<String, Object>> pinsLeft = (1..7).collect { index ->
        ['id': index]
    }

    List<Map<String, Object>> pinsTop = []

    List<Map<String, Object>> pinsRight = ((8..14).collect { index ->
        ['id': index]
    }).reverse()

    List<Map<String, Object>> pinsBottom = []

    public ICSpecificationModel() {
    }
        
    public ICSpecificationModel(ICSpecificationModel model) {
        this.specification = model.specification
        this.pinCount = model.pinCount
        this.value = model.value
        this.pinsLeft = model.pinsLeft
        this.pinsTop = model.pinsTop
        this.pinsRight = model.pinsRight
        this.pinsBottom = model.pinsBottom
    }
}
