package dev.matiaspg.annotationsmapping.utils.annotations;

import com.fasterxml.jackson.databind.JsonNode;
import dev.matiaspg.annotationsmapping.annotations.MapEachFrom;
import dev.matiaspg.annotationsmapping.annotations.MapFrom;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = """
    dev.matiaspg.mapping.annotationReplacements=[\\
      {\\
        "targetClass": "dev.matiaspg.annotationsmapping.utils.annotations.AnnotationsProviderTest$TestClass",\\
        "targetField": "testField",\\
        "annotationClass": "dev.matiaspg.annotationsmapping.annotations.MapFrom",\\
        "replacements": {\\
          "value": "/testField_replaced"\\
        }\\
      },\\
      {\\
        "targetClass": "dev.matiaspg.annotationsmapping.utils.annotations.AnnotationsProviderTest$TestClass",\\
        "targetMethod": "testMethod",\\
        "annotationClass": "dev.matiaspg.annotationsmapping.annotations.MapEachFrom",\\
        "replacements": {\\
          "value": "/testMethod_replaced"\\
        }\\
      },\\
      {\\
        "targetClass": "dev.matiaspg.annotationsmapping.utils.annotations.AnnotationsProviderTest$TestClass",\\
        "targetMethod": "testMethodWithParams",\\
        "targetMethodParam": "aField",\\
        "annotationClass": "dev.matiaspg.annotationsmapping.annotations.MapFrom",\\
        "replacements": {\\
          "value": "/testMethodWithParams/aField_replaced"\\
        }\\
      }\\
    ]
    """
)
public class AnnotationsProviderTest {
    @Autowired
    private AnnotationsProvider annotationsProvider;

    @Test
    void testOverrides() throws NoSuchFieldException, NoSuchMethodException {
        Class<TestClass> testClass = TestClass.class;

        // Original field annotation value
        Field testField = testClass.getDeclaredField("testField");
        assertEquals("/testField",
            testField.getAnnotation(MapFrom.class).value());

        // Overridden field annotation value
        MapFrom testFieldMapFrom = annotationsProvider
            .getAnnotation(testField, MapFrom.class);
        assertEquals("/testField_replaced", testFieldMapFrom.value());

        // Original method annotation value
        Method testMethod = testClass
            .getDeclaredMethod("testMethod", List.class);
        assertEquals("/testMethod",
            testMethod.getAnnotation(MapEachFrom.class).value());

        // Overridden method annotation value
        MapEachFrom testMethodMapEachFrom = annotationsProvider
            .getAnnotation(testMethod, MapEachFrom.class);
        assertEquals("/testMethod_replaced", testMethodMapEachFrom.value());

        // Original method parameter annotation value
        Method testMethodWithParams = testClass
            .getDeclaredMethod("testMethodWithParams", String.class, List.class);
        Parameter firstParameter = testMethodWithParams.getParameters()[0];
        assertEquals("/testMethodWithParams/aField",
            firstParameter.getAnnotation(MapFrom.class).value());

        // Overridden method parameter annotation value
        MapFrom firstParameterMapFrom = annotationsProvider
            .getAnnotation(firstParameter, MapFrom.class);
        assertEquals("/testMethodWithParams/aField_replaced", firstParameterMapFrom.value());
    }

    @Data
    public static class TestClass {
        @MapFrom("/testField")
        private String testField;

        @MapEachFrom("/testMethod")
        private void testMethod(List<JsonNode> items) {
        }

        private void testMethodWithParams(
            @MapFrom("/testMethodWithParams/aField") String aField,
            @MapEachFrom("/testMethodWithParams/anotherField") List<JsonNode> anotherField
        ) {
        }
    }
}
