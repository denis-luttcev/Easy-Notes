package ru.z8.louttsev.easynotes.database;

public class NotesDBSchema {

    public static final class NotesTable {
        public static final String NAME = "notes";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TYPE = "type";
            public static final String TITLE = "title";
            public static final String CATEGORY = "category";
            public static final String COLOR = "color";
            public static final String DEADLINE = "deadline";
            public static final String LAST_MODIFICATION = "modification";
            public static final String CONTENT = "content";
        }
    }

    public static final class CategoriesTable {
        public static final String NAME = "categories";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
        }

    }

    public static final class TagsTable {
        public static final String NAME = "tags";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
        }
    }

    public static final class TaggingTable {
        public static final String NAME = "tagging";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String NOTE = "note";
            public static final String TAG = "tag";
        }
    }
}
