a() {
    i: int = 10
    while (i < 20) {
        if (i < 2) {
            break
        }
        if (i < 2) {
            if (i > 4) {
                break
            }
        } else {
            break
        }
        i = i + 1
    }
    while (i < 20) {
        break
    }
}
