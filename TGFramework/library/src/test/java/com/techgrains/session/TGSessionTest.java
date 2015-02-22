/*
 * Copyright 2015 Techgrains Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.techgrains.session;

import org.junit.*;

import static org.junit.Assert.*;

public class TGSessionTest {

    TGSession session;

    @Before
    public void setUp() {
        session = TGSession.instance();
    }

    @After
    public void tearDown() {
        session.invalidate();
    }

    @Test
    public void isValid() {
        assertTrue(session.isValid());
        session.invalidate();
        assertFalse(session.isValid());
    }

    @Test
    public void sessionId() {
        try {
            long id1 = session.id();
            session.invalidate();
            Thread.sleep(2);
            session = TGSession.instance();
            long id2 = session.id();
            assertNotEquals(id1, id2);
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void validSince() {
        try {
            Thread.sleep(2);
            long validSince = session.validSince();
            assertTrue(validSince > 0);
            assertTrue(validSince < 10); // Execution shouldn't take more than 10 millisecond since setup.
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void getPutRemoveAttribute() {
        String key = "First Name";
        String value = "Vishal";
        session.put(key, value);
        assertEquals(value, session.get(key));
        assertNull(session.get("random"));
        session.remove(key);
        assertNull(session.get(key));
    }

    @Test
    public void hasKey() {
        String key = "First Name";
        String value = "Vishal";
        session.put(key, value);
        assertTrue(session.hasKey(key));
    }

    @Test
    public void keySet() {
        String key1 = "Key 1";
        String key2 = "Key 2";
        String key3 = "Key 3";
        session.put(key1, null);
        session.put(key2, null);

        assertTrue(session.keys().contains(key1));
        assertTrue(session.keys().contains(key2));
        assertFalse(session.keys().contains(key3));
    }

    @Test
    public void sessionListener() {
        TestSessionListener listener = new TestSessionListener();
        session.addSessionListener(listener);
        assertFalse(listener.isSessionInvalidate);

        // sessionInvalidate()
        session.invalidate();
        assertTrue(listener.isSessionInvalidate);
        listener.reset();

        // sessionCreated()
        session = TGSession.instance();
        assertFalse(listener.isSessionInvalidate);

        // Listener won't be called after removing listener
        session.removeSessionListener(listener);
        listener.reset();
        session.invalidate();
        assertFalse(listener.isSessionInvalidate);
        session = TGSession.instance();
        assertFalse(listener.isSessionInvalidate);
    }

    @Test
    public void sessionListenerCount() {
        TestSessionListener listener1 = new TestSessionListener();
        TestSessionListener listener2 = new TestSessionListener();
        session.addSessionListener(listener1);
        assertEquals(1, session.countSessionListeners());
        session.addSessionListener(listener2);
        assertEquals(2, session.countSessionListeners());
        session.addSessionListener(listener1);
        assertEquals(2, session.countSessionListeners());
        session.removeSessionListener(listener2);
        assertEquals(1, session.countSessionListeners());
        session.addSessionListener(listener2);
        session.removeAllSessionListeners();
        assertEquals(0, session.countSessionListeners());
    }

    @Test
    public void multipleSessionListeners() {
        TestSessionListener listener1 = new TestSessionListener();
        TestSessionListener listener2 = new TestSessionListener();
        session.addSessionListener(listener1);
        session.addSessionListener(listener2);
        assertFalse(listener1.isSessionInvalidate);
        assertFalse(listener2.isSessionInvalidate);
        session.invalidate();
        assertTrue(listener1.isSessionInvalidate);
        assertTrue(listener2.isSessionInvalidate);
    }

    @Test
    public void equals() {
        try {
            TGSession session1 = session;
            TGSession session2 = TGSession.instance();
            session.invalidate();
            Thread.sleep(2);
            TGSession session3 = TGSession.instance();

            assertTrue(session1.equals(session2));
            assertFalse(session2.equals(session3));
        } catch (InterruptedException e) {
            fail(e.getMessage());
        }
    }

    public class TestSessionListener implements TGSessionListener {
        boolean isSessionInvalidate;

        public void sessionInvalidate() {
            isSessionInvalidate = true;
        }

        public void reset() {
            isSessionInvalidate = false;
        }
    }
}
