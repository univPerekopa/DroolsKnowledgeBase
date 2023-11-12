/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.examples.relationships;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import java.util.HashMap;
import java.util.Map;

public class Relationships {

    public static void main(final String[] args) {
        KieContainer kc = KieServices.Factory.get().getKieClasspathContainer();
        execute( kc );
    }

    public static void execute( KieContainer kc ) {
        KieSession ksession = kc.newKieSession("RelationshipsKS");

        Person amy = new Person( "Amy");
        Person bob = new Person( "Bob");
        Person charlie = new Person( "Charlie");
        Person daniel = new Person( "Daniel");

        bob.setParent(amy);
        charlie.setParent(amy);
        daniel.setParent(bob);

        ksession.insert(amy);
        ksession.insert(bob);
        ksession.insert(charlie);
        ksession.insert(daniel);

        ksession.fireAllRules();

        Map<String, StringBuilder> relationshipsByType = new HashMap<>();
        // Iterate over all facts in the working memory
        for (FactHandle factHandle : ksession.getFactHandles()) {
            // Retrieve the object associated with the fact handle
            Object factObject = ksession.getObject(factHandle);

            // Check if the object is an instance of Relationship
            if (factObject instanceof Relationship) {
                Relationship relationship = (Relationship) factObject;
                relationshipsByType
                        .computeIfAbsent(relationship.getRelationshipType1(), k -> new StringBuilder())
                        .append(relationship).append("\n");
            }
        }

        // Print relationships grouped by relationType
        for (Map.Entry<String, StringBuilder> entry : relationshipsByType.entrySet()) {
            System.out.println("RelationType: " + entry.getKey());
            System.out.println(entry.getValue());
        }


        ksession.dispose(); // Stateful rule session must always be disposed when finished
    }


    public static class Person {
        private String name;
        private Person parent;

        public Person(String name) {
            this.name = name;
        }

        public Person(String name, Person parent) {
            this.name = name;
            this.parent = parent;
        }

        public String getName() {
            return name;
        }

        public Person getParent() {
            return parent;
        }

        public void setParent(Person parent) {
            this.parent = parent;
        }
    }



    public static class Relationship {
        private Person person1;
        private Person person2;
        private String relationshipType1;
        private String relationshipType2;

        public Relationship(Person person1, Person person2, String relationshipType1, String relationshipType2) {
            this.person1 = person1;
            this.person2 = person2;
            this.relationshipType1 = relationshipType1;
            this.relationshipType2 = relationshipType2;
        }

        public Person getPerson1() {
            return person1;
        }

        public Person getPerson2() {
            return person2;
        }

        public String getRelationshipType1() {
            return relationshipType1;
        }

        public String getRelationshipType2() {
            return relationshipType2;
        }

        @Override
        public String toString() {
            return String.format("%s is %s of %s, and %s is %s of %s",
                    person1.getName(), relationshipType1, person2.getName(),
                    person2.getName(), relationshipType2, person1.getName());
        }
    }
}
