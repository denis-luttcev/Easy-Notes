package ru.z8.louttsev.easynotes.datamodel;

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

public abstract class Note implements Comparable<Note>, Cloneable {

    public enum Color {
        URGENT, ATTENTION, NORMAL, QUIET, ACCESSORY, NONE
    }

    public enum DeadlineStatus {
        OVERDUE, IMMEDIATE, AHEAD, NONE
    }

    /**
     * Unique id (primary key), null not allowable
     */
    private UUID id;
    private String title;
    private Category category;
    private Map<String, Tag> tags;
    private Color color;
    private Calendar deadline;
    private Calendar lastModification;
    private boolean isModified;

    Note() {
        id = UUID.randomUUID();
        title = "";
        category = null;
        color = Color.NONE;
        tags = new HashMap<>();
        deadline = null;
        modificationUpdate();
        isModified = false;
    }

    /**
     * Comparison for base sorting implementation:
     * (1) any deadlined note before any not deadlined
     * (2) among deadlined notes: by deadline ascending order, for equals rule (3)
     * (3) among not deadlined notes: by last modification descending order
     */
    @Override
    public int compareTo(@NonNull Note note) {
        if (this.equals(note)) return 0;

        if (deadline != null) {
            if (note.deadline != null) {
                int deadlineComparing = deadline.compareTo(note.deadline);
                if (deadlineComparing != 0) {
                    return deadlineComparing;
                }
            } else return -1;
        } else {
            if (note.deadline != null) return 1;
        }

        return note.lastModification.compareTo(lastModification);
    }

    void modificationUpdate() {
        lastModification = Calendar.getInstance();
        isModified = true;
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
        this.title = title;
        modificationUpdate();
    }

    public boolean hasTitle(@NonNull String title) {
        if (isTitled()) {
            return this.title.equals(title);
        }
        return false;
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

    @NonNull
    public Set<Tag> getTags() {
        return new HashSet<>(tags.values());
    }

    public void markTag(@NonNull Tag tag) {
        tags.put(tag.getTitle(), tag);
        modificationUpdate();
    }

    public void unmarkTag(@NonNull String title) {
        if (hasTag(title)) {
            tags.remove(title);
            modificationUpdate();
        }
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

    public void setDeadline(@Nullable Calendar deadline) {
        this.deadline = deadline;
        modificationUpdate();
    }

    private boolean isOverdue(@NonNull Calendar toDate) throws NullPointerException {
        return deadline.before(toDate);
    }

    private boolean isImmediate(@NonNull Calendar toDate) throws NullPointerException {
        return isToday(deadline);
    }

    private boolean isAhead(@NonNull Calendar toDate) throws NullPointerException {
        return deadline.after(toDate);
    }

    @NonNull
    public DeadlineStatus getDeadlineStatus(@NonNull Calendar toDate) {
        try {
            if (isOverdue(toDate)) return DeadlineStatus.OVERDUE;
            if (isImmediate(toDate)) return DeadlineStatus.IMMEDIATE;
            if (isAhead(toDate)) return DeadlineStatus.AHEAD;
        } catch (NullPointerException ignored) {}
        return DeadlineStatus.NONE;
    }

    public boolean isDeadlined() {
        return deadline != null;
    }

    @NonNull
    public String getDeadlineRepresent(@NonNull Context context) {
        StringBuilder dateRepresent = new StringBuilder();
        if (isYesterday(deadline)) {
            dateRepresent.append(context.getString(R.string.yesterday));
        } else if (isToday(deadline)) {
            dateRepresent.append(context.getString(R.string.today));
        } else if (isTomorrow(deadline)) {
            dateRepresent.append(context.getString(R.string.tomorrow));
        } else {
            DateFormat dateFormat = SimpleDateFormat.getDateInstance(DateFormat.SHORT);
            dateRepresent.append(dateFormat.format(deadline.getTime()));
            dateRepresent.append(" ");
        }
        DateFormat timeFormat = SimpleDateFormat.getTimeInstance(DateFormat.SHORT);
        dateRepresent.append(timeFormat.format(deadline.getTime()));
        return dateRepresent.toString();
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

    public abstract void fillContentPreView(@NonNull FrameLayout contentPreView, Context context);

    public abstract void fillContentView(@NonNull FrameLayout contentView, Context context);

    public abstract void setContent(@NonNull FrameLayout contentView);

    public abstract boolean isContentEmpty();

    //TODO: remove
    public abstract void setContent(String content);
}
