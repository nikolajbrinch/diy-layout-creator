package org.diylc.core.registry

import static org.junit.Assert.assertEquals

import org.diylc.core.components.properties.PropertyModel;
import org.diylc.core.components.registry.SpecificationReader;
import org.junit.Test

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper

public class TestSpecificationReader {

    @Test
    public void test4013in() throws JsonParseException, JsonMappingException, IOException {
        PropertyModel value = new SpecificationReader().read(new File("./src/main/resources/specifications/ic/4013.spec"))

        assertEquals("IC", value.category)
        assertEquals("4013N", value.name)
    }

    @Test
    public void testAllIn() throws JsonParseException, JsonMappingException, IOException {
        def models = [
            "4013",
            "4014",
            "4021",
            "74hc164",
            "74hc165",
            "74hc166",
            "74hc595",
            "74ls06",
            "74ls07",
            "attiny85",
            "ir4428",
            "lm334",
            "lm386",
            "max7219",
            "max7221",
            "mc34063",
            "mic4420",
            "moc3023",
            "ne555",
            "pc817",
            "tlc5940",
            "tpic6b595",
            "tsop4828",
            "uln2803"
        ]

        models.each { String model ->
            SpecificationReader reader = new SpecificationReader()
            PropertyModel value = reader.read(new File("./src/main/resources/specifications/ic/${model}.spec"))
            assertEquals("IC", value.category)
            println(new ObjectMapper().writeValueAsString(value))
        }
    }
}
