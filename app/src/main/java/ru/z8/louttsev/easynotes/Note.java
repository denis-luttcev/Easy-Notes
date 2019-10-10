package ru.z8.louttsev.easynotes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

abstract class Note {

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

    Note() {
        this.id = UUID.randomUUID();
        this.title = null;
        this.category = null;
        this.color = Color.NONE;
        this.tags = new HashSet<>();
        this.deadline = null;
    }

    @Nullable String getTitle() {
        return title;
    }

    void setTitle(@Nullable String title) {
        this.title = title;
    }

    boolean hasTitle(@NonNull String title) throws NullPointerException {
        return this.title.equals(title);
    }

    boolean isTitled() {
        return this.title != null;
    }

    @Nullable Category getCategory() {
        return category;
    }

    void setCategory(@Nullable Category category) {
        this.category = category;
    }

    boolean hasCategory(@NonNull Category category) throws NullPointerException {
        return this.category.equals(category);
    }

    boolean isCategorized() {
        return this.category != null;
    }

    @NonNull Color getColor() {
        return color;
    }

    void setColor(@NonNull Color color) {
        this.color = color;
    }

    boolean hasColor(@NonNull Color color) {
        return this.color == color;
    }

    boolean isColored() {
        return this.color != Color.NONE;
    }

    void addTag(@NonNull Tag tag) {
        this.tags.add(tag);
    }

    void removeTag(@NonNull Tag tag) {
        if (hasTag(tag)) {
            this.tags.remove(tag);
        }
    }

    boolean hasTag (@NonNull Tag tag) {
        return this.tags.contains(tag);
    }

    boolean isTagged() {
        return !this.tags.isEmpty();
    }

    @Nullable Calendar getDeadline() {
        return deadline;
    }

    void setDeadline(@Nullable Calendar deadline) {
        this.deadline = deadline;
    }

    boolean isOverdue(@NonNull Calendar toDate) throws NullPointerException {
        return this.deadline.before(toDate);
    }

    boolean isImmediate(@NonNull Calendar toDate) throws NullPointerException {
        return this.deadline.equals(toDate);
    }

    boolean isAhead(@NonNull Calendar toDate) throws NullPointerException {
        return this.deadline.after(toDate);
    }

    @NonNull DeadlineStatus getDeadlineStatus(@NonNull Calendar toDate) {
        try {
            if (isOverdue(toDate)) return DeadlineStatus.OVERDUE;
            if (isImmediate(toDate)) return DeadlineStatus.IMMEDIATE;
            if (isAhead(toDate)) return DeadlineStatus.AHEAD;
        } catch (NullPointerException ignored) {};
        return DeadlineStatus.NONE;
    }

    boolean isDeadlined() {
        return this.deadline != null;
    }
}
