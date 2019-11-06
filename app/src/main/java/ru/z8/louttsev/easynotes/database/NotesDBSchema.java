package ru.z8.louttsev.easynotes.database;

class NotesDBSchema {

    static final class NotesTable {
        static final String NAME = "notes";

        static final class Cols {
            static final String UUID = "uuid";
            static final String TYPE = "type";
            static final String TITLE = "title";
            static final String CATEGORY = "category";
            static final String COLOR = "color";
            static final String DEADLINE = "deadline";
            static final String LAST_MODIFICATION = "modification";
            static final String CONTENT = "content";
        }
    }

    static final class CategoriesTable {
        static final String NAME = "categories";

        static final class Cols {
            static final String UUID = "uuid";
            static final String TITLE = "title";
        }

    }

    static final class TagsTable {
        static final String NAME = "tags";

        static final class Cols {
            static final String UUID = "uuid";
            static final String TITLE = "title";
        }
    }

    static final class TaggingTable {
        static final String NAME = "tagging";

        static final class Cols {
            static final String UUID = "uuid";
            static final String NOTE = "note";
            static final String TAG = "tag";
        }
    }
}
