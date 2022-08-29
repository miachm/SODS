/*
 * Copyright 2019 Philippe Charles.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.miachm.sods;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.dom.DOMResult;
import org.testng.annotations.Test;
import static org.testng.AssertJUnit.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Philippe Charles
 */
public class OfficeValueTypeTest {

    @Test
    public void testReadBoolean() throws Exception {
        assertRead(OfficeValueType.BOOLEAN, "office:boolean-value", "true")
                .isEqualTo(Boolean.TRUE);

        assertRead(OfficeValueType.BOOLEAN, "office:boolean-value", "false")
                .isEqualTo(Boolean.FALSE);

        assertRead(OfficeValueType.BOOLEAN, "office:boolean-value", "123")
                .isNull();

        assertRead(OfficeValueType.BOOLEAN, "office:boolean-value", "")
                .isNull();

        assertRead(OfficeValueType.BOOLEAN, "", "true")
                .isNull();
    }

    @Test
    public void testWriteBoolean() throws Exception {
        assertWrite(OfficeValueType.BOOLEAN, true)
                .containsAttribute("office:value-type", "boolean")
                .containsAttribute("office:boolean-value", "true");

        assertWrite(OfficeValueType.BOOLEAN, false)
                .containsAttribute("office:value-type", "boolean")
                .containsAttribute("office:boolean-value", "false");

        assertWrite(OfficeValueType.BOOLEAN, "")
                .doesNotContainAttribute("office:value-type")
                .doesNotContainAttribute("office:boolean-value");

        assertWrite(OfficeValueType.BOOLEAN, null)
                .doesNotContainAttribute("office:value-type")
                .doesNotContainAttribute("office:boolean-value");
    }

    @Test
    public void testReadCurrency() {
    }

    @Test
    public void testWriteCurrency() {
    }

    @Test
    public void testReadDate() throws Exception {
        assertRead(OfficeValueType.DATE, "office:date-value", "2003-04-17")
                .isEqualTo(LocalDate.of(2003, 4, 17));

        assertRead(OfficeValueType.DATE, "office:date-value", "2003-04-17T03:30:00")
                .isEqualTo(LocalDateTime.of(2003, 4, 17, 3, 30, 0));

        assertRead(OfficeValueType.DATE, "office:date-value", "")
                .isNull();

        assertRead(OfficeValueType.DATE, "", "2003-04-17")
                .isNull();
    }

    @Test
    public void testWriteDate() throws Exception {
        assertWrite(OfficeValueType.DATE, LocalDate.of(2003, 4, 17))
                .containsAttribute("office:value-type", "date")
                .containsAttribute("office:date-value", "2003-04-17");

        assertWrite(OfficeValueType.DATE, LocalDateTime.of(2003, 4, 17, 3, 30, 0))
                .containsAttribute("office:value-type", "date")
                .containsAttribute("office:date-value", "2003-04-17T03:30:00");

        assertWrite(OfficeValueType.DATE, "")
                .doesNotContainAttribute("office:value-type")
                .doesNotContainAttribute("office:date-value");

        assertWrite(OfficeValueType.DATE, null)
                .doesNotContainAttribute("office:value-type")
                .doesNotContainAttribute("office:date-value");
    }

    @Test
    public void testReadFloat() throws Exception {
        assertRead(OfficeValueType.FLOAT, "office:value", "3.14")
                .isEqualTo(3.14);

        assertRead(OfficeValueType.FLOAT, "office:value", "")
                .isNull();

        assertRead(OfficeValueType.FLOAT, "", "3.14")
                .isNull();
    }

    @Test
    public void testWriteFloat() throws Exception {
        assertWrite(OfficeValueType.FLOAT, 3.14)
                .containsAttribute("office:value-type", "float")
                .containsAttribute("office:value", "3.14");

        assertWrite(OfficeValueType.FLOAT, "")
                .doesNotContainAttribute("office:value-type")
                .doesNotContainAttribute("office:value");

        assertWrite(OfficeValueType.FLOAT, null)
                .doesNotContainAttribute("office:value-type")
                .doesNotContainAttribute("office:value");
    }

    @Test
    public void testReadPercentage() {
    }

    @Test
    public void testWritePercentage() {
    }

    @Test
    public void testReadString() throws Exception {
        assertRead(OfficeValueType.STRING, "office:string-value", "hello")
                .isEqualTo("hello");

        assertRead(OfficeValueType.STRING, "", "hello")
                .isNull();
    }

    @Test
    public void testWriteString() throws Exception {

    }

    @Test
    public void testReadTime() throws Exception {
        assertRead(OfficeValueType.TIME, "office:time-value", "PT03H30M00S")
                .isEqualTo(Duration.ofMinutes(3 * 60 + 30));

        assertRead(OfficeValueType.TIME, "office:time-value", "")
                .isNull();

        assertRead(OfficeValueType.TIME, "", "PT03H30M00S")
                .isNull();
    }

    @Test
    public void testWriteTime() throws Exception {
        assertWrite(OfficeValueType.TIME, Duration.ofMinutes(3 * 60 + 30))
                .containsAttribute("office:value-type", "time")
                .containsAttribute("office:time-value", "PT3H30M");

        assertWrite(OfficeValueType.TIME, "")
                .doesNotContainAttribute("office:value-type")
                .doesNotContainAttribute("office:time-value");

        assertWrite(OfficeValueType.TIME, null)
                .doesNotContainAttribute("office:value-type")
                .doesNotContainAttribute("office:time-value");
    }

    @Test
    public void testReadVoid() {
    }

    @Test
    public void testWriteVoid() {
    }

    private static ObjectAssert assertRead(OfficeValueType valueType, String key, String value) throws Exception {
        return new ObjectAssert(valueType.read(new FakeReader(Collections.singletonMap(key, value))));
    }

    private static NodeAssert assertWrite(OfficeValueType valueType, Object value) throws Exception {
        return NodeAssert.of(writer -> {
            writer.writeStartDocument();
            writer.writeStartElement("root");
            valueType.write(value, writer);
            writer.writeEndElement();
            writer.writeEndDocument();
        });
    }

    private static final class FakeReader implements XmlReaderInstance {

        private final Map<String, String> attributes;

        public FakeReader(Map<String, String> attributes) {
            this.attributes = attributes;
        }

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public XmlReaderInstance nextElement(String... name) {
            return null;
        }

        @Override
        public XmlReaderInstance nextElement(Set<String> name) {
            return null;
        }

        @Override
        public String getAttribValue(String name) {
            return attributes.get(name);
        }

        @Override
        public String getContent() {
            return null;
        }

        @Override
        public String getTag() {
            return null;
        }
    }

    private static final class ObjectAssert {

        private final Object target;

        public ObjectAssert(Object object) {
            this.target = object;
        }

        public ObjectAssert isNull() {
            assertNull(target);
            return this;
        }

        public ObjectAssert isEqualTo(Object expected) {
            assertEquals(expected, target);
            return this;
        }
    }

    private static final class NodeAssert {

        public interface XMLStreamConsumer {

            void apply(XMLStreamWriter writer) throws XMLStreamException;
        }

        public static NodeAssert of(XMLStreamConsumer consumer) throws XMLStreamException, ParserConfigurationException {
            Document result = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            XMLStreamWriter writer = XMLOutputFactory.newFactory().createXMLStreamWriter(new DOMResult(result));
            consumer.apply(writer);
            writer.close();
            return new NodeAssert(result.getDocumentElement());
        }

        private final Element target;

        public NodeAssert(Element node) {
            this.target = node;
        }

        public NodeAssert containsAttribute(String attrKey, String attrValue) {
            assertTrue(target.hasAttribute(attrKey));
            assertEquals(attrValue, target.getAttributeNode(attrKey).getValue());
            return this;
        }

        public NodeAssert doesNotContainAttribute(String attrKey) {
            assertFalse(target.hasAttribute(attrKey));
            return this;
        }
    }
}
