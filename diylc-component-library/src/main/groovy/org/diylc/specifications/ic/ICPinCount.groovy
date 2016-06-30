package org.diylc.specifications.ic

enum ICPinCount {

    _3, _4, _6, _8, _10, _12, _14, _16, _18, _20, _22, _24, _26, _28, _30, _32, _34, _36, _38, _40, _42, _44, _46, _48, _50

    @Override
    public String toString() {
        return name().replace("_", "")
    }

    public int getValue() {
        return Integer.parseInt(toString())
    }

    public static ICPinCount getPinCount(int pinCount) {
        return ICPinCount.valueOf("_" + String.valueOf(pinCount));
    }
}
