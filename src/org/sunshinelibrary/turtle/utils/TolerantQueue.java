package org.sunshinelibrary.turtle.utils;

import com.squareup.tape.FileObjectQueue;

import java.io.File;
import java.io.IOException;

/**
 * @author linuo
 * @version 1.0
 * @date 11/29/13
 */
public class TolerantQueue<T> extends FileObjectQueue<T> {
    public TolerantQueue(File file, Converter<T> converter) throws IOException {
        super(file, converter);
    }

    @Override
    public T peek() {
        while (true) {
            try {
                return super.peek();
            } catch (RuntimeException e) {
                e.printStackTrace();
                super.remove();
            }
        }
    }
}
