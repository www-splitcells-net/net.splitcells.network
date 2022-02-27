package net.splitcells.website.server.project.renderer.commonmark;

import net.splitcells.dem.data.set.list.List;
import org.commonmark.node.AbstractVisitor;
import org.commonmark.node.ListItem;
import org.commonmark.node.Node;
import org.commonmark.node.Paragraph;
import org.commonmark.node.StrongEmphasis;
import org.commonmark.node.Text;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

import static net.splitcells.dem.data.set.list.Lists.list;

public class EventExtractor extends AbstractVisitor {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Pattern DATE_TIME_PATTERN = Pattern.compile("[0-9][0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9]");

    public static EventExtractor eventExtractor() {
        return new EventExtractor();
    }

    private List<Event> extractedEvents = list();

    private EventExtractor() {

    }

    @Override
    public void visit(ListItem listItem) {
        final var itemContent = listItem.getFirstChild();
        if (itemContent.getFirstChild() == null) {
            visitChildren(listItem);
            return;
        }
        final var possibleDate = itemContent.getFirstChild();
        System.out.println(possibleDate.toString());
        if (possibleDate instanceof StrongEmphasis) {
            final var possibleDateEmphasis = (StrongEmphasis) possibleDate;
            final var possibleDateText = ((Text) possibleDateEmphasis.getFirstChild()).getLiteral();
            if (DATE_TIME_PATTERN.matcher(possibleDateText).matches()) {
                final var dateTime = LocalDate.parse(possibleDateText, DATE_TIME_FORMATTER);
                extractedEvents.add(Event.event(LocalDateTime.of(dateTime, LocalTime.of(0, 0)), listItem));
            }
        }
        visitChildren(listItem);
    }

    public List<Event> extractedEvents() {
        return extractedEvents;
    }
}
