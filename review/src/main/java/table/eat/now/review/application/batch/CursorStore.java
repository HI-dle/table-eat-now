package table.eat.now.review.application.batch;

public interface CursorStore {

  void saveCursor(String key, Cursor context);

  Cursor getCursor(String key);
}
