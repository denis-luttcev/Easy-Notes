package ru.z8.louttsev.easynotes.datamodel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

abstract class Note implements ContentContainer, Comparable<Note> {

    enum Color {
        RED, ORANGE, YELLOW, GREEN, BLUE, NONE
    }

    enum DeadlineStatus {
        OVERDUE, IMMEDIATE, AHEAD, NONE
    }

    private UUID id;
    private String title;
    private Category category;
    private Set<Tag> tags;
    private Color color;
    private Calendar deadline;
    private Calendar lastModification;

    Note() {
        id = UUID.randomUUID();
        title = null;
        category = null;
        color = Color.NONE;
        tags = new HashSet<>();
        deadline = null;
        updateLastModification();
    }

    @Override
    public int compareTo(@NonNull Note note) {
        if (this.equals(note)) return 0;

        if (deadline != null) {
            if (note.deadline != null) {
                int deadlineComparing = deadline.compareTo(note.deadline);
                if (deadlineComparing != 0) {
                    return deadlineComparing;
                }
            } else return 1;
        } else {
            if (note.deadline != null) return -1;
        }

        return lastModification.compareTo(note.lastModification);
    }

    private void updateLastModification() {
        lastModification = Calendar.getInstance();
    }

    @NonNull
    UUID getId() {
        return id;
    }

    @Nullable
    String getTitle() {
        return title;
    }

    void setTitle(@NonNull String title) {
        if (!title.trim().isEmpty()) {
            this.title = title;
            updateLastModification();
        } else {
            this.title = null;
        }
    }

    void clearTitle() {
        title = null;
        updateLastModification();
    }

    boolean hasTitle(@NonNull String title) throws NullPointerException {
        return this.title.equals(title);
    }

    boolean isTitled() {
        return title != null;
    }

    @Nullable
    Category getCategory() {
        return category;
    }

    void setCategory(@Nullable Category category) {
        this.category = category;
        updateLastModification();
    }

    boolean hasCategory(@NonNull Category category) throws NullPointerException {
        return this.category.equals(category);
    }

    boolean isCategorized() {
        return category != null;
    }

    @NonNull
    Color getColor() {
        return color;
    }

    void setColor(@NonNull Color color) {
        this.color = color;
        updateLastModification();
    }

    boolean hasColor(@NonNull Color color) {
        return this.color == color;
    }

    boolean isColored() {
        return color != Color.NONE;
    }

    @NonNull
    Set<Tag> getTags() {
        return tags;
    }

    void addTag(@NonNull Tag tag) {
        tags.add(tag);
        updateLastModification();
    }

    void removeTag(@NonNull Tag tag) {
        if (hasTag(tag)) {
            tags.remove(tag);
            updateLastModification();
        }
    }

    boolean hasTag (@NonNull Tag tag) {
        return tags.contains(tag);
    }

    boolean isTagged() {
        return !tags.isEmpty();
    }

    @Nullable
    Calendar getDeadline() {
        return deadline;
    }

    void setDeadline(@Nullable Calendar deadline) {
        this.deadline = deadline;
        updateLastModification();
    }

    boolean isOverdue(@NonNull Calendar toDate) throws NullPointerException {
        return deadline.before(toDate);
    }

    boolean isImmediate(@NonNull Calendar toDate) throws NullPointerException {
        return deadline.equals(toDate);
    }

    boolean isAhead(@NonNull Calendar toDate) throws NullPointerException {
        return deadline.after(toDate);
    }

    @NonNull
    DeadlineStatus getDeadlineStatus(@NonNull Calendar toDate) {
        try {
            if (isOverdue(toDate)) return DeadlineStatus.OVERDUE;
            if (isImmediate(toDate)) return DeadlineStatus.IMMEDIATE;
            if (isAhead(toDate)) return DeadlineStatus.AHEAD;
        } catch (NullPointerException ignored) {}
        return DeadlineStatus.NONE;
    }

    boolean isDeadlined() {
        return deadline != null;
    }

    @NonNull
    Calendar getLastModification() {
        return lastModification;
    }

    boolean isModified(@NonNull Calendar toDate) {
        return lastModification.after(toDate);
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        if (that == null || getClass() != that.getClass()) return false;
        Note note = (Note) that;
        return id.equals(note.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
