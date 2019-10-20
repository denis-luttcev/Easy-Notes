package ru.z8.louttsev.easynotes.datamodel;

import android.content.Context;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public abstract class Note implements Comparable<Note> {

    public enum Color {
        URGENT, ATTENTION, NORMAL, QUIET, ACCESSORY, NONE
    }

    public enum DeadlineStatus {
        OVERDUE, IMMEDIATE, AHEAD, NONE
    }

    private UUID id;
    private String title;
    private Category category;
    private Set<Tag> tags;
    private Color color;
    private Calendar deadline;
    private Calendar lastModification;

    public Note() {
        id = UUID.randomUUID();
        title = null;
        category = null;
        color = Color.NONE;
        tags = new HashSet<>();
        deadline = null;
        modificationUpdate();
    }

    @Override
    public int compareTo(@NonNull Note note) {
        if (this.equals(note)) return 0;

        if (deadline != null) {
            if (note.deadline != null) {
                int deadlineComparing = deadline.compareTo(Objects.requireNonNull(note.getDeadline()));
                if (deadlineComparing != 0) {
                    return deadlineComparing;
                }
            } else return -1;
        } else {
            if (note.deadline != null) return 1;
        }

        return note.getLastModification().compareTo(lastModification);
    }

    private void modificationUpdate() {
        lastModification = Calendar.getInstance();
        //TODO: point to write new or edited note to DB
    }

    @NonNull
    public UUID getId() {
        return id;
    }

    @Nullable
    public String getTitle() {
        return title;
    }

    public void setTitle(@Nullable String title) {
        if (title != null && !title.trim().isEmpty()) {
            this.title = title;
        } else {
            this.title = null;
        }
        modificationUpdate();
    }

    public boolean hasTitle(@NonNull String title) {
        if (isTitled()) {
            return this.title.equals(title);
        }
        return false;
    }

    public boolean isTitled() {
        return title != null;
    }

    @Nullable
    public Category getCategory() {
        return category;
    }

    public void setCategory(@Nullable Category category) {
        this.category = category;
        modificationUpdate();
    }

    public boolean hasCategory(@NonNull Category category) {
        if (isCategorized()) {
            return this.category.equals(category);
        }
        return false;
    }

    public boolean isCategorized() {
        return category != null;
    }

    @NonNull
    public Color getColor() {
        return color;
    }

    public void setColor(@NonNull Color color) {
        this.color = color;
        modificationUpdate();
    }

    public boolean hasColor(@NonNull Color color) {
        return this.color == color;
    }

    public boolean isColored() {
        return color != Color.NONE;
    }

    @Nullable
    public Set<Tag> getTags() {
        if (isTagged()) {
            return tags;
        }
        return null;
    }

    public void markTag(@NonNull Tag tag) {
        tags.add(tag);
        modificationUpdate();
    }

    public void unmarkTag(@NonNull Tag tag) {
        if (hasTag(tag)) {
            tags.remove(tag);
            modificationUpdate();
        }
    }

    public boolean hasTag(@NonNull Tag tag) {
        return tags.contains(tag);
    }

    public boolean isTagged() {
        return !tags.isEmpty();
    }

    @Nullable
    public Calendar getDeadline() {
        return deadline;
    }

    public void setDeadline(@Nullable Calendar deadline) {
        this.deadline = deadline;
        modificationUpdate();
    }

    public boolean isOverdue(@NonNull Calendar toDate) throws NullPointerException {
        return deadline.before(toDate);
    }

    public boolean isImmediate(@NonNull Calendar toDate) throws NullPointerException {
        return deadline.equals(toDate);
    }

    public boolean isAhead(@NonNull Calendar toDate) throws NullPointerException {
        return deadline.after(toDate);
    }

    @NonNull
    public DeadlineStatus getDeadlineStatus(@NonNull Calendar toDate) {
        try {
            if (isAhead(toDate)) return DeadlineStatus.AHEAD;
            if (isImmediate(toDate)) return DeadlineStatus.IMMEDIATE;
            if (isOverdue(toDate)) return DeadlineStatus.OVERDUE;
        } catch (NullPointerException ignored) {}
        return DeadlineStatus.NONE;
    }

    public boolean isDeadlined() {
        return deadline != null;
    }

    @NonNull
    public Calendar getLastModification() {
        return lastModification;
    }

    public boolean isModified(@NonNull Calendar toDate) {
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

    public abstract boolean isEditable();

    public abstract void fillContentPreView(@NonNull FrameLayout contentPreView, Context context);

    public abstract void fillContentView(@NonNull FrameLayout contentView, Context context);

    public abstract void setContent(@NonNull FrameLayout contentView);

    //TODO: remove
    public abstract void setContent(String content);
}
