use io
use conv

fn(count: int, input: int): int {
    if (count == 0) {
        return 100;
    }

    a: int = 5;
    b: int = a;
    c: int = b;
    d: int = b + 5;
    e: int = c + 10;
    f: int = a + c;
    g: int = d;
    h: int = a;
    i: int = h;
    j: int = 5
    k: int = i;
    l: int = e + c;
    m: int = e - c + b - a;
    n: int = h - a + c - l;
    o: int = n;
    p: int = o;
    q: int = p;
    r: int = q;
    s: int = r;
    t: int = s;
    u: int = t;
    v: int = s + p + q;
    w: int = t;
    x: int = a + c - p;
    y: int = k + a + o + r;
    z: int = k;

    return fn(count - 1, input + a * b - c * d + e * f - g * h + i * j - k * l
        + m * n - o * p + q * r - s * t + u * v - w * x + y * z);
}

main(args: int[][]) {
    counter: int = 0;
    acc: int = 0;
    while (counter < 1000) {
        a: int = 5;
        b: int = a;
        c: int = b;
        d: int = b + 5;
        e: int = c + 10;
        f: int = a + c;
        g: int = d;
        h: int = a;
        i: int = h;
        j: int = 5
        k: int = i;
        l: int = e + c;
        m: int = e - c + b - a;
        n: int = h - a + c - l;
        o: int = n;
        p: int = o;
        q: int = p;
        r: int = q;
        s: int = r;
        t: int = s;
        u: int = t;
        v: int = s + p + q;
        w: int = t;
        x: int = a + c - p;
        y: int = k - a + o - r;
        z: int = k;

        result: int =
            fn(10, a + b + c + d + e + f + g + h + i
            + j + k + l + m + n + o + p + q + r
            + s + t + u + v + w + x + y + z);

        acc = acc + result;
        counter = counter + 1;
    }
}