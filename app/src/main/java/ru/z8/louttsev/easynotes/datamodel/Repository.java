package ru.z8.louttsev.easynotes.datamodel;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

class Repository implements NotesKeeper {
    private Set<Category> categories = new HashSet<>();
    private Set<Tag> tags = new HashSet<>();
    private SortedMap<UUID, Note> notes = new TreeMap<>();
}
