package com.example.steamapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.stream.Stream;

@SpringBootApplication
public class SteamApiApplication {
    record Car(String type, String make, String model, Integer engineCapacity) {
    }

    public static void main(String[] args) {
        SpringApplication.run(SteamApiApplication.class, args);

        List<Car> cars = List.of(
                new Car("sedan", "BMW", "530", 1998),
                new Car("sedan", "Mercedes", "E-Class", 1999),
                new Car("sedan", "Audi", "A5", 1984),
                new Car("suv", "Toyota", "RAV4", 1987),
                new Car("suv", "Honda", "CR-V", 1997),
                new Car("hatchback", "Volkswagen", "Golf", 1395),
                new Car("coupe", "Ford", "Mustang", 4951),
                new Car("pickup", "Ford", "Ranger", 1996)
        );
        // filter
        List<Car> sedanCars = cars.stream().filter(car -> car.type.equals("sedan")).toList();

        // map
        List<String> carMakeList = cars.stream().map(car-> car.make).toList();

        // kết hợp giữa map và filter
        List<String> carMakeNotEmptyList = cars.stream()
                .map(car -> car.make)
                .filter(make -> !make.isEmpty())
                .toList();

        // trả về Audi và A5, Mercedes và E-class
        // Nếu mình dùng map trả về 1 cặp như vậy thì nó phải nhận dữ liệu là List<List<String>>
        // nhưng mình không muốn như vậy thì dùng flatMap, nhưng nó lại nhận 1 stream thay vì 1 list
        // vậy mình sẽ ep kiểu sang steam bằng method .stream() và rút gọn thì nhìn phía dưới dòng rút gọn
        List<String> carmakeModelList = cars.stream().flatMap(car -> List.of(car.make, car.model).stream()).toList();

        // dòng rút gọn
        List<String> carmakeModelListRutGon = cars.stream().flatMap(car -> Stream.of(car.make, car.model)).toList();

        /*
        Trong Java, Lazy Evaluation (đánh giá lười biếng) là một đặc tính quan trọng của Stream API. Hiểu một cách đơn giản,
        nó có nghĩa là các thao tác trên Stream không được thực hiện ngay lập tức khi chúng được khai báo,
        mà chỉ thực sự chạy khi có một Terminal Operation (thao tác kết thúc) được gọi.

        Để hiểu Lazy Evaluation, bạn cần phân biệt:
            - Intermediate Operations (Thao tác trung gian): Như filter(), map(), sorted(), limit(), distinct().
            Những hàm này luôn trả về một Stream mới. Chúng không thực hiện bất kỳ tính toán nào mà chỉ ghi lại (record) các bước cần làm.
            - Terminal Operations (Thao tác kết thúc): Như collect(), forEach(), reduce(), count(), findFirst().
            Khi một hàm này được gọi, Stream mới bắt đầu duyệt qua dữ liệu và thực hiện các bước đã ghi lại trước đó.
        */
        Stream<Integer> integerStream = Stream.of(10, 11, 12, 13, 14);

        Stream<Integer> filteredIntegerStream = integerStream.filter(i -> {
            System.out.println("Filtering integer");
            return i % 2 == 0;
        });

        System.out.println("Count = " + filteredIntegerStream.count());
    }

}
