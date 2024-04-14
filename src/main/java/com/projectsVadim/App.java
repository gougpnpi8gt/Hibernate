package com.projectsVadim;

import com.projectsVadim.ManyTOMany.Actor;
import com.projectsVadim.ManyTOMany.Movie;
import com.projectsVadim.TestTasksModel.Principal;
import com.projectsVadim.TestTasksModel.School;
import com.projectsVadim.model.Item;
import com.projectsVadim.model.Passport;
import com.projectsVadim.model.Person;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        //автоматически ищет файл с конфигурацией и находит
        Configuration configuration = new Configuration()
                .addAnnotatedClass(Person.class)
                .addAnnotatedClass(Item.class)
                .addAnnotatedClass(Passport.class)
                .addAnnotatedClass(Principal.class)
                .addAnnotatedClass(School.class)
                .addAnnotatedClass(Movie.class)
                .addAnnotatedClass(Actor.class);
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        Session session = sessionFactory.getCurrentSession();
        try{
            session.beginTransaction();
            Person person = session.get(Person.class, 1);
            System.out.println(person);



            //Hibernate.initialize(person.getItems()); // - метод, который подгружать связанные сущности

            // Если к аннотации добавить Fetch.Earn - автоматически подгрузятся все товару(так как hibernate делает left join в запросе к БД)
            // Если за пределами commit'а(Detach-состояние) вызвать метод получения товаров, то вне сессии они загрузятся, ошибок не будет
            // Если fetch.LAZY, то возник ошибка
//            Person person = session.get(Person.class, 1);
//            System.out.println(person.getItems());

//            // будет один запрос, так как неленивая загрузка на стороне товара - fetch.earn(один ко многим)
//            Item item = session.get(Item.class, 1);
//            System.out.println("Получили товар");
//            System.out.println(item.getOwner());
//
//
//            // будет два запроса к базе данных, так как на стороне человека fetch.Lazy - ленивая загрузка
//            Person person = session.get(Person.class, 1);
//            System.out.println("Получили человека");
//            //Получим связанные сущности
//            System.out.println(person.getItems());


            session.getTransaction().commit();
            //session.close()
            System.out.println("Сессия закрылась");
            session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            //ОТкрываем сессия и транзакцию еще раз в любом месте кода
            System.out.println("Внутри второй транзакции");
            person = (Person) session.merge(person); // возвращает данный объект из Detach состояния в контекст Persisten
            Hibernate.initialize(person.getItems()); // подгружаю и товары будут доступны вне текущей сессии
            /*
            Второй метод с помощью HQL-запроса
            Необязательно добавлять сущность Person - (Person) session.merge(person), так как можно с легкостью получить id
            List<Item> items = session.createQuery("select i from Item i where i.owner.id=:personId", Item.class)
                    .setParameter("personId", person.getId()).getResultList();
            System.out.println(items)
             */
            session.getTransaction().commit();
            System.out.println("Вне второй сессии");
            // Это работает, как как связанные товары были загружены
            System.out.println(person.getItems());

        } finally {
            sessionFactory.close();
        }




//            Actor actor = session.get(Actor.class, 2);
//            System.out.println(actor.getMovies());
//            Movie movieToRemove = actor.getMovies().get(0);
//            actor.getMovies().remove(0);
//            movieToRemove.getActors().remove(actor); // чтобы удалить такой объект, нужен hashCode and Equals


            // Добавление актера в фильм, сохраняем этот фильм, а актер будет сохранен - поскольку находится в perсisten состоянии
//            Movie movie = new Movie("Reservoir Dogs", 1992);
//            Actor actor = session.get(Actor.class, 1);
//            movie.setActors(new ArrayList<>(Collections.singletonList(actor)));
//            actor.getMovies().add(movie);
//            session.save(movie);



            // Нет каскадирования, сохраняем фильм и актеров
//            Movie movie = new Movie("Pulp fiction", 1994);
//            Actor actor1 = new Actor("Harvey Keitel", 81);
//            Actor actor2 = new Actor("Samuel L. Jackson", 72);
//            movie.setActors(new ArrayList<>(List.of(actor1, actor2)));
//            actor1.setMovies(new ArrayList<>(Collections.singletonList(movie)));
//            actor2.setMovies(new ArrayList<>(Collections.singletonList(movie)));
//            session.save(movie);
//            session.save(actor1);
//            session.save(actor2);


//            Principal principal = new Principal("Olik", 30);
//            School school = session.get(School.class, 2);
//            school.setPrincipal(principal);
//            session.save(principal);
//            session.save(school);


//            Principal principal = new Principal("vadik", 23);
//            School school = new School(17, principal);
//            principal.setSchool(school);
//            session.save(principal);
//
//            School school = session.get(School.class, 1);
//            System.out.println(school.getPrincipal());


            //            Principal principal = session.get(Principal.class, 1);
//            System.out.println(principal.getSchool());





//            Passport passport = session.get(Passport.class, 1);
//            System.out.println(passport.getPerson().getName());
//            Person person = session.get(Person.class, 1);
//            session.remove(person);
//            Person person = session.get(Person.class, 1);
//            person.getPassport().setPassportNumber(7777);




//            Person person = new Person("Test Person2",60);
//            // лишння SQL команда возникает так как создается запрос - есть ли паспорт с данным человек в таблице
//            Passport passport = new Passport(123456);
//
//            person.setPassport(passport);
//            session.save(person);







/*
            Каскадирование
 */
//            Person person = new Person("testik cascading", 30);
//            person.addItem(new Item("Test cascading item1"));
//            person.addItem(new Item("Test cascading item2"));
//            person.addItem(new Item("Test cascading item3"));
//            //session.persist(person);//При сохранении одной сущности автоматически сохраняются другие связанные сущности
//            session.save(person);


            /*
            Отношение один ко многим, жизненный цикл в hibernate
            - trainset - создание объекта, hibernate не отслеживает этот объект
            - detach - объект отслеживается hibernate при сохранении или работе с базой данных
            -  merge - возвращение в persist context
            - removed - удаляется соответсвующим методом
             */

//            Person person = session.get(Person.class, 4);
//            Item item = session.get(Item.class, 1);
//            item.getOwner().getItems().remove(item);
//
//            item.setOwner(person);
//
//            person.getItems().add(item);

//            Person person = session.get(Person.class, 2);
//            //SQL
            //session.remove(person);
//            // Было правильное состояние Hibernate кэша
//            person.getItems().forEach(i -> i.setOwner(null));



            //SQL
//            Person person = session.get(Person.class, 3);
//            List<Item> items = person.getItems();
//            for (Item item : items){
//                session.remove(item);
//            }
//            // не порождает SQL, необходимо для кэша
//            person.getItems().clear();


//            Person person = new Person("Test person 2", 20);
//            Item item = new Item("Item from hibernate 2", person);
//            person.setItems(new ArrayList<>(Collections.singletonList(item)));// создает список из одного элемента, но он не изменяемый, чтобы был изменяем- помещаем в ArrayList
//            session.save(person);
//            session.save(item);



//            Person person = session.get(Person.class, 2);
//            Item newItem = new Item("Item from hibernate", person);
//            person.getItems().add(newItem);//мы гарантировано сохранили новый товар у человека в кэш hibernate
//            session.save(newItem);

//            Item item = session.get(Item.class, 5);
//            System.out.println(item);
//            Person person = item.getOwner();
//            System.out.println(person);
//            Person person = session.get(Person.class, 3);
//            System.out.println(person);
//            List<Item> items = person.getItems();
//            items.stream().forEach(System.out::println);
    }
}
