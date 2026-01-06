package com.example.steamapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

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
                new Car("suv", "Volkswagen", "Golf", 1395),
                new Car("suv", "Ford2", "Mustang", 4951),
                new Car("suv", "Ford", "Ranger", 1996)
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

        // Partitioning by collector: Trong khi groupingBy có thể chia dữ liệu thành nhiều nhóm khác nhau dựa trên
        // bất kỳ giá trị nào (ví dụ: nhóm theo màu sắc, theo hãng xe), thì partitioningBy chỉ chia dữ liệu thành đúng
        // hai nhóm dựa trên một điều kiện đúng/sai (Boolean).
        /*
        - cars.stream(): Tạo một dòng dữ liệu (stream) từ danh sách các đối tượng Car.
        - .collect(...): Đây là Terminal Operation dùng để thu thập kết quả của stream vào một cấu trúc dữ liệu khác (ở đây là Map).
        - partitioningBy(car -> car.type.equals("sedan")):
            Đây là một collector đặc biệt. Nó nhận vào một Predicate (một hàm trả về true/false).
            Nó sẽ kiểm tra từng chiếc xe: chiếc nào có loại là "sedan" thì cho vào nhóm true, chiếc nào không phải thì cho vào nhóm false.
        - Map<Boolean, List<Car>>: Kết quả trả về luôn là một Map có đúng 2 key:
            true: Chứa danh sách (List<Car>) các xe là sedan.
            false: Chứa danh sách (List<Car>) các xe không phải là sedan.
        */
        Map<Boolean, List<Car>> partitionedCars = cars.stream().collect(
                partitioningBy(car -> car.type.equals("sedan")
        ));

        System.out.println("partitionedCars = " + partitionedCars);

        // grouping by collector
        // (type, (make, engineCapacity))
        // Car::type thay cho param -> param.type
        /*
        - groupingBy(Car::type, ...): Đầu tiên, nó chia tất cả các xe vào các nhóm dựa trên type (ví dụ: "Sedan", "SUV").
            Kết quả tạm thời là một Map<String, List<Car>>.
        - toMap(...): Thay vì để mỗi nhóm là một List<Car> mặc định, bạn dùng toMap để tái cấu trúc danh sách các xe đó.
        - Key-Value của Map con: Với mỗi nhóm xe cùng loại, nó lấy tên hãng (make) làm Key và dung tích động cơ (engineCapacity) làm Value.
        */
        Map<String, Map<String, Integer>> groupedCarts = cars.stream().collect(
                groupingBy(
                        Car::type,
                        toMap(
                                Car::make,
                                Car::engineCapacity
                        )
                )
        );

        System.out.println("grouped cars = " + groupedCarts);

        /*
            - Parallel Streams (Dòng song song) là một tính năng của Java Stream API
            cho phép tận dụng các bộ vi xử lý đa nhân (multi-core CPUs) để thực hiện các thao tác trên dữ liệu một cách đồng thời.
            - Thay vì xử lý tuần tự từng phần tử một (Sequential Stream),
            Parallel Stream chia dữ liệu thành nhiều phần nhỏ, xử lý chúng trên các luồng (threads) khác nhau, sau đó gộp kết quả lại.
            - Parallel Stream không phải lúc nào cũng nhanh hơn Stream tuần tự
        */
    }

}
