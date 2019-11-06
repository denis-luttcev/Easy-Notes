package ru.z8.louttsev.easynotes.datamodel;

import android.content.ContentValues;
import android.content.Context;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import ru.z8.louttsev.easynotes.R;
import ru.z8.louttsev.easynotes.database.NotesCursorWrapper;

public abstract class Note implements Comparable<Note>, Cloneable {

    public enum Type {
        TEXT_NOTE
        // new types of notes should be listed here
    }

    public enum Color {
        URGENT, ATTENTION, NORMAL, QUIET, ACCESSORY, NONE
    }

    public enum Status {
        OVERDUE, IMMEDIATE, AHEAD, NONE
    }

    private final UUID id;
    private String title;
    private Category category;
    private Map<String, Tag> tags;
    private Color color;
    private Calendar deadline;
    private Calendar lastModification;
    private boolean isModified;

    Note(@NonNull UUID id) {
        this.id = id;
        title = "";
        category = null;
        color = Color.NONE;
        tags = new HashMap<>();
        deadline = null;
        lastModification = Calendar.getInstance();
        isModified = false;
    }

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    @NonNull
    public static Note newInstance(@NonNull Type noteType) throws IllegalArgumentException {
        switch (noteType) {
            case TEXT_NOTE:
                return new TextNote();
            // calls to the constructors of new concrete classes should be placed here
            default:
                throw new IllegalArgumentException(); // unreachable
        }
    }

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    @NonNull
    public static Note getInstance(@NonNull Type noteType, UUID id) throws IllegalArgumentException {
        switch (noteType) {
            case TEXT_NOTE:
                return new TextNote(id);
            // calls to the constructors of new concrete classes should be placed here
            default:
                throw new IllegalArgumentException(); // unreachable
        }
    }

    @Override
    public int compareTo(@NonNull Note that) {
        /* Comparison for base sorting implementation:
            (1) any deadlined before any not deadlined
            (2) among deadlined: by deadline ascending order, for equals: use rule (3)
            (3) among not deadlined: by last modification descending order */

        if (this.equals(that)) return 0;

        if (deadline != null) {
            if (that.deadline != null) {
                int deadlineComparing = deadline.compareTo(that.deadline);
                if (deadlineComparing != 0) {
                    return deadlineComparing;
                }
            } else return -1;
        } else {
            if (that.deadline != null) return 1;
        }

        return that.lastModification.compareTo(lastModification);
    }

    @NonNull
    public UUID getId() {
        return id;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        if (!title.equals(this.title)) {
            this.title = title;
            modificationUpdate();
        }
    }

    void modificationUpdate() {
        lastModification = Calendar.getInstance();
        isModified = true;
    }

    public boolean isTitled() {
        return !title.isEmpty();
    }

    @Nullable
    public Category getCategory() {
        return category;
    }

    public void setCategory(@Nullable Category category) {
        this.category = category;
        modificationUpdate();
    }

    public boolean hasCategory(@NonNull String title) {
        if (isCategorized()) {
            return category.getTitle().equals(title);
        } return false;
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

    public boolean isColored() {
        return color != Color.NONE;
    }

    @NonNull
    public Set<Tag> getTags() {
        return new HashSet<>(tags.values());
    }

    public void markTag(@NonNull Tag tag) {
        tags.put(tag.getTitle(), tag);
        modificationUpdate();
    }

    public void unmarkTag(@NonNull String title) {
        tags.remove(title);
        modificationUpdate();
    }

    public boolean hasTag(@NonNull String title) {
        return tags.containsKey(title);
    }

    public boolean isTagged() {
        return !tags.isEmpty();
    }

    @Nullable
    public Calendar getDeadline() {
        return deadline;
    }

    @NonNull
    public String getDeadline(@NonNull Context context) {
        StringBuilder deadline = new StringBuilder();

        if (isYesterday(this.deadline)) {
            deadline.append(context.getString(R.string.yesterday));
        } else if (isToday(this.deadline)) {
            deadline.append(context.getString(R.string.today));
        } else if (isTomorrow(this.deadline)) {
            deadline.append(context.getString(R.string.tomorrow));
        } else {
            DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.SHORT);
            deadline.append(dateFormat.format(this.deadline.getTime()));
            deadline.append(" ");
        }

        DateFormat timeFormat = SimpleDateFormat.getTimeInstance(DateFormat.SHORT);
        deadline.append(timeFormat.format(this.deadline.getTime()));

        return deadline.toString();
    }

    private boolean isToday(@NonNull Calendar date) {
        Calendar today = Calendar.getInstance();

        return date.get(Calendar.DATE) == today.get(Calendar.DATE)
                && date.get(Calendar.MONTH) == today.get(Calendar.MONTH)
                && date.get(Calendar.YEAR) == today.get(Calendar.YEAR);
    }

    private boolean isYesterday(@NonNull Calendar date) {
        Calendar nextDay = (Calendar) date.clone();
        nextDay.add(Calendar.DATE, 1);

        return isToday(nextDay);
    }

    private boolean isTomorrow(@NonNull Calendar date) {
        Calendar prevDay = (Calendar) date.clone();
        prevDay.add(Calendar.DATE, -1);

        return isToday(prevDay);
    }

    public void setDeadline(@Nullable Calendar deadline) {
        this.deadline = deadline;
        modificationUpdate();
    }

    @NonNull
    public Status getStatus() {
        try {
            if (isOverdue()) return Status.OVERDUE;
            if (isImmediate()) return Status.IMMEDIATE;
            if (isAhead()) return Status.AHEAD;
        } catch (NullPointerException ignored) {}
        return Status.NONE;
    }

    private boolean isOverdue() throws NullPointerException {
        return deadline.before(Calendar.getInstance());
    }

    private boolean isImmediate() {
        return isToday(deadline);
    }

    private boolean isAhead() throws NullPointerException {
        return deadline.after(Calendar.getInstance());
    }

    public boolean isDeadlined() {
        return deadline != null;
    }

    public void setLastModification(Calendar lastModification) {
        this.lastModification = lastModification;
    }

    @NonNull
    public Calendar getLastModification() {
        return lastModification;
    }

    public boolean isModified() {
        return isModified;
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

    @NonNull
    @Override
    public Note clone() throws CloneNotSupportedException {
        Note clone = (Note) super.clone();
        clone.tags = new HashMap<>(this.tags);
        return clone;
    }

    @SuppressWarnings("SameReturnValue")
    public abstract Type getType();

    public abstract void fillContentPreView(@NonNull FrameLayout contentPreView, @NonNull Context context);

    public abstract void fillContentView(@NonNull FrameLayout contentView, @NonNull Context context);

    public abstract void setContent(@NonNull FrameLayout contentView);

    public abstract boolean isContentEmpty();

    public abstract void getContentForDB(@NonNull String key, @NonNull ContentValues values);

    public abstract void setContentFromDB(@NonNull String key, @NonNull NotesCursorWrapper cursor);
}
