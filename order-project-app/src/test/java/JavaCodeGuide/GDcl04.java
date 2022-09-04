package JavaCodeGuide;

public class GDcl04 {



}

/**
 * 在新增枚举常量时可能会导致原有枚举常量的值发生变化
 */
enum BadKeyboard{
    MOUSE_KEY_LEFT,
    MOUSE_KEY_RIGHT,
    MOUSE_KEY_CANCEL,
    MOUSE_KEY_MIDDLE;

    public int getKeyValue() {
        return ordinal() + 1;
    }
}

/**
 * 当新增枚举常量时，避免了原有枚举常量值发生变化。
 */
enum GoodKeyBoard{

    MOUSE_KEY_LEFT(1),
    MOUSE_KEY_RIGHT(2),
    MOUSE_KEY_CANCEL(4),
    MOUSE_KEY_MIDDLE(8);

    private final int keyValue;

    GoodKeyBoard(int keyValue) {
        this.keyValue = keyValue;
    }

    public int getKeyValue() {
        return keyValue;
    }
}