class Vehicle {
    position: int
    move(x: int) { position = position + x }
    initVehicle(pos: int): Vehicle {
        position = pos
        return this
    }
}

class Car extends Vehicle {
    passengers: int
    await(v: Vehicle) {
        if (v.position < position) {
            v.move(position - v.position)
        } else {
            this.move(10)
        }
    }
    initCar(pos: int, pass: int): Car {
        _ = initVehicle(pos)
        passengers = pass
        return this
    }
}

class Truck extends Vehicle {
    move(x: int) {
        if (x <= 55) {
            position = position + x
        }
    }
    initTruck(pos: int): Truck {
        _ = initVehicle(pos)
        return this
    }
}

main(args:int[][]) {
    t: Truck = new Truck.initTruck(0)
    c: Car = new Car.initCar(0, 10)
    v: Vehicle = c
    c.passengers = 2
    c.move(60)
    v.move(70)
    c.await(t)
}
