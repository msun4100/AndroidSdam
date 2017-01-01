package kr.me.sdam.detail.tokenexample;

import java.io.Serializable;

/**
 * Simple container object for contact data
 *
 * Created by mgod on 9/12/13.
 * @author mgod
 */
public class Person implements Serializable{
    private String name;

    public Person(String n) {
        name = n;
    }

    public String getName() { return name; }
    @Override
    public String toString() { return name; }
}
