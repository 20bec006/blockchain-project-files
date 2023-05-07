package simblock.utils;

import java.io.IOException;
import java.io.InputStream;

public interface Parser<T> {
  T parse(InputStream is) throws IOException;

}
