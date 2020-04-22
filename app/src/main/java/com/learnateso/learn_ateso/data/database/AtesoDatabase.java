package com.learnateso.learn_ateso.data.database;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import android.content.Context;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import android.util.Log;

import com.learnateso.learn_ateso.data.AtesoRepository;
import com.learnateso.learn_ateso.models.Category;
import com.learnateso.learn_ateso.models.*;

/**
 * Created by BE on 2/3/2018.
 */
@Database(entities = {Category.class, User.class, Section.class,
        WorkBook.class, Phrase.class}, version = 2, exportSchema = false)
public abstract class AtesoDatabase extends RoomDatabase {
    private static final String TAG = AtesoDatabase.class.getSimpleName();

    public abstract CategoriesDao categoriesDao();
    public abstract UsersDao usersDao();
    public abstract WorkBookDao workBookDao();
    public abstract SectionsDao sectionsDao();
    public abstract PhrasesDao phrasesDao();
    private static AtesoRepository repository = AtesoRepository.getInstance();
    private static AtesoDatabase INSTANCE;

    public static AtesoDatabase getDatabase (final Context context){
        if (INSTANCE == null){
            synchronized (AtesoDatabase.class){
                if (INSTANCE == null){
                    //create db here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AtesoDatabase.class, "LearnAteso.db")
                            .addCallback(sRoomDatabaseCallback)
                            .addMigrations(MIGRATION_1_2)
                            /**
                             * uncomment during production release
                             */
                            //.fallbackToDestructiveMigrationFrom()// used if we dnt want to provide migrations
                            //and specifically want the db to be cleared
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    //populate database when the app is started.
    //Create a RoomDatabase.Callback and override onOpen()
    //if you use "onOpen()" the db will be recreated every time the app is started
    //while "onCreate()" the db will remain the same
    private static  RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback(){
                @Override
                public void onCreate (@NonNull SupportSQLiteDatabase db){
                    super.onCreate(db);
                    //int numPhrases = repository.countAllPhrases();
                    //if(numPhrases < 1) {
                        new PopulateDbAsync(INSTANCE).execute();
                        Log.e(TAG, "populating db");
                    /*}else{
                        Log.e(TAG, "Db already populated");
                    }*/
                }
            };

    //migrating from db v1 to v2
    static final Migration MIGRATION_1_2 = new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //since we didnt alter the table, there's nothing else to do here
        }
    };

    private static Section addSection(AtesoDatabase db, int id, String sectionName,
                                   String sectionImage, int categoryId){
        Section section = new Section();
        section.setSectionId(id);
        section.setSectionName(sectionName);
        section.setSectionImage(sectionImage);
        section.setCategoryId(categoryId);
        db.sectionsDao().insertSection(section);

        return section;
    }

    private static Category addCategory(AtesoDatabase db, int category_id, String category_name, String category_image){
        Category category = new Category();
        category.setCategoryId(category_id);
        category.setCategoryImage(category_image);
        category.setCategoryName(category_name);
        db.categoriesDao().insertCategory(category);

        return category;
    }

    private static WorkBook addLesson(AtesoDatabase db, int id, String ateso_word, String audio,
                                      String hint, String opt_1,String opt_2,String opt_3,String opt_4,
                                      String answer,String pic_name1,String pic_name2,String pic_name3,
                                      String pic_name4,
                                      String pic_audio1,String pic_audio2,String pic_audio3,String pic_audio4,
                                      String english_word, String saved_audio, String ateso_word_match1,
                                      String ateso_word_match2,String ateso_word_match3, String eng_word_match1,
                                      String ateso_word_match_audio2,String ateso_word_match_audio3,
                                      String ateso_word_match_audio1, String eng_word_match2,String eng_word_match3,
                                      String ateso_audio_comparison1,String ateso_audio_comparison2,
                                      String eng_word_match4,String eng_word_match5,String sentConstPhrase, String sentConstAns,
                                      String exercise_name, int workBookSectionId, int workBookCategoryId){
        WorkBook workBook = new WorkBook();
        workBook.setExerciseID(id);
        workBook.setAtesoWord(ateso_word);
        workBook.setAudio(audio);
        workBook.setHint(hint);
        workBook.setOpt_1(opt_1);
        workBook.setOpt_2(opt_2);
        workBook.setOpt_3(opt_3);
        workBook.setOpt_4(opt_4);
        workBook.setAnswer(answer);
        workBook.setPic_name1(pic_name1);
        workBook.setPic_name2(pic_name2);
        workBook.setPic_name3(pic_name3);
        workBook.setPic_name4(pic_name4);
        workBook.setPic_audio1(pic_audio1);
        workBook.setPic_audio2(pic_audio2);
        workBook.setPic_audio3(pic_audio3);
        workBook.setPic_audio4(pic_audio4);
        workBook.setEnglish_word(english_word);
        workBook.setSaved_audio(saved_audio);
        workBook.setAwordMatch1(ateso_word_match1);
        workBook.setAwordMatch2(ateso_word_match2);
        workBook.setAwordMatch3(ateso_word_match3);
        workBook.setAwordMatchAudio1(ateso_word_match_audio1);
        workBook.setAwordMatchAudio2(ateso_word_match_audio2);
        workBook.setAwordMatchAudio3(ateso_word_match_audio3);
        workBook.setAtesoComparisonAudio1(ateso_audio_comparison1);
        workBook.setAtesoComparisonAudio2(ateso_audio_comparison2);
        workBook.setEngWordMatch1(eng_word_match1);
        workBook.setEngWordMatch2(eng_word_match2);
        workBook.setEngWordMatch3(eng_word_match3);
        workBook.setEngWordMatch4(eng_word_match4);
        workBook.setEngWordMatch5(eng_word_match5);
        workBook.setSentConstPhrase(sentConstPhrase);
        workBook.setSentConstAns(sentConstAns);
        workBook.setExercise_name(exercise_name);
        workBook.setWorkBookSectionId(workBookSectionId);
        workBook.setWorkBookCategoryId(workBookCategoryId);

        db.workBookDao().insertLesson(workBook);

        return workBook;
    }

    private static Phrase addPhrase(AtesoDatabase db, int phraseId, String phrase,
                                    String audio, String translation, int isFavourite, int sectionId, int categoryId){
        Phrase atesoPhrase = new Phrase();
        atesoPhrase.setPhraseId(phraseId);
        atesoPhrase.setAtesoPhrase(phrase);
        atesoPhrase.setAtesoAudio(audio);
        atesoPhrase.setTranslation(translation);
        atesoPhrase.setIsFavourite(isFavourite);
        atesoPhrase.setPhraseSectionId(sectionId);
        atesoPhrase.setPhraseCategoryId(categoryId);

        db.phrasesDao().insertPhrase(atesoPhrase);

        return atesoPhrase;
    }

    private static void populateWithTestData(AtesoDatabase db) {
        db.categoriesDao().deleteAll();
        db.sectionsDao().deleteAll();
        db.workBookDao().deleteAll();
        db.phrasesDao().deleteAll();

        Section section1 = addSection(db, 1, "Greetings", "greet", 1);
        Section section2 = addSection(db, 2, "Numbers", "numbers", 1);
        Section section3 = addSection(db, 3, "Expressing Gratitude", "gratitude", 1);
        //Section section4 = addSection(db, 4, "Drinks", "food", 2);
        Section section5 = addSection(db, 5, "Days", "days", 12);
        Section section6 = addSection(db, 6, "Months", "months", 12);
        Section section7 = addSection(db, 7, "Directions", "directions", 13);
        Section section8 = addSection(db, 8, "Family", "family", 7);
        Section section9 = addSection(db, 9, "Body parts", "body_parts", 4);
        Section section10 = addSection(db, 10, "Wardrobe", "wardrobe", 5);

        Category category1 = addCategory(db, 1, "Basics", "basics");
        //Category category2 = addCategory(db, 2, "Food", "food");
        //Category category3 = addCategory(db, 3, "Animals", "animals");
        Category category4 = addCategory(db, 4, "Human Body", "bodyparts");
        Category category5 = addCategory(db, 5, "Clothing", "clothing");
        Category category7 = addCategory(db, 7, "Relations", "relations");
        Category category12 = addCategory(db, 12, "Calendar", "weekdays");
        Category category13 = addCategory(db, 13, "Travel", "vehicle");

        //TODO insert lesson rows
        /**
         *
         * Start of Greetings - basics
         * */
        WorkBook workBook1 = addLesson(db,1,"Ijai biai?","ijai_biai","Greeting someone","Hello",
                "How are you?","How's life?","Hey there","How are you?",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",1,1);
        WorkBook workBook2 = addLesson(db,2,"Ijai biai?","ikwenyunit_biai","Greeting someone",null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                "Good morning","Ikwenyunit biai",
                "sentence_construction",1,1);
        WorkBook workBook3 = addLesson(db,3,"Yoga","yoga","Greeting someone","Hello",
                "How are you?","How's life?","Hey there","Hello",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",1,1);
        WorkBook workBook4 = addLesson(db,4,"Ijai biai?","ijaasi_biai","Greeting many",null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                "How are you?","Ijaasi biai?",
                "sentence_construction",1,1);

        WorkBook workBook5 = addLesson(db,5,"Ejokuna, arai ijo?","ejokuna_arai_ijo","Responding to a greeting","I am fine, thanks",
                "I am well","I am fine, how are you?","I am fine","I am fine, how are you?",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",1,1);
        WorkBook workBook6 = addLesson(db,6,"Ekakiror erai...","ekakiror_erai","What is your name","The name is...",
                "I am...","I am called...","My name is...","My name is...",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",1,1);
        WorkBook workBook7 = addLesson(db,7,"Ingai bo ekon kiror?","ingai_bo_ekon_kiror","asking for the name",null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                "What is your name?","Ingai bo ekon kiror?",
                "sentence_construction",1,1);

        WorkBook workBook8 = addLesson(db,8,"Eyalama airiamun","eyalama_ariamun","happy to meet someone","You are welcome",
                "Thanks for meeting","Pleased to meet you","Thank you","Pleased to meet you",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",1,1);
        WorkBook workBook9 = addLesson(db,9,"Eyalama awanyun","eyalama_awanyun","happy to see someone","Happy for you",
                "Pleased to see you","Pleased to find you","Thank you","Pleased to see you",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",1,1);
        WorkBook workBook10 = addLesson(db,10,"Iboikin ber","iboikin_ber","sit down","Please sit",
                "Take a seat","First sit","Sit now","Please sit",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",1,1);
        WorkBook workBook11 = addLesson(db,11,"Yoga do","yoga_do","paring with someone","Hi there",
                "See you","Hello","Good bye","Good bye",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",1,1);
        WorkBook workBook12 = addLesson(db,12,"Ojotor ojok","ojotor_ojok","off to bed","Good sleep",
                "Good night","Sleep well","Sleep tight","Sleep well",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",1,1);
        /**
        *
        * end of Greetings - basics
        * */

        /**
         *
         * Start of Numbers - basics
         * */

        WorkBook workBook13 = addLesson(db,13,"Abakasa adiopet","abakasa_ediopet",null,null,
                null,null,null,"one_envelope","one_envelope",
                "nine_groundnut_husks","two_buses","foureggs","abakasa_ediopet",
                "abubuk_akanyaongon","abaasin_aare","abeei_aongon","One envelope",null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "picture_quiz",2,1);

        WorkBook
                workBook14 = addLesson(db,14,"Abuwai atomon","abuwai_atomon","some number of caves","10 rocks",
                "21 caves","Ten caves","Six balls","Ten caves",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",2,1);

        WorkBook workBook15 = addLesson(db,15,"Abeei aongon","abeei_aongon",null,null,
                null,null,null,"foureggs","nine_groundnut_husks",
                "foureggs","one_envelope","two_buses","abubuk_akanyaongon",
                "abeei_aongon","abakasa_ediopet","abaasin_aare","Four eggs",null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "picture_quiz",2,1);

        WorkBook workBook16 = addLesson(db,16,"Imwalimun akais-akany'ape alu kany'aare",
                "imwalimun_akaisakanyape_alu_kanyaare","some number of caves","Sixty teachers",
                "Sixty seven teachers","Seven teachers","Six balls","Sixty seven teachers",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",2,1);

        WorkBook workBook17 = addLesson(db,17,"Abaasin aare","abaasin_aare",null,null,
                null,null,null,"two_buses","one_envelope",
                "nine_groundnut_husks","foureggs","two_buses","abakasa_ediopet",
                "abubuk_akanyaongon","abeei_aongon","abaasin_aare","2 buses",null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "picture_quiz",2,1);
        /**
         *
         * end of Numbers - basics
         * */

        /**
         *
         * Start of Expressing Gratitude - basics
         * */

        WorkBook
                workBook18 = addLesson(db,18,"Eyalama","eyalama","When you are grateful " +
                        "for something done/given","Thank you",
                "I am grateful","Thanks to you","Thank you all","Thank you",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",3,1);

        WorkBook
                workBook19 = addLesson(db,19,"Eyalamikini eong noi","eyalamikini_eong_noi","When you are grateful " +
                        "for something done/given","Thank you so much",
                "I was so grateful","I will be so grateful","I am so grateful","I will be so grateful",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",3,1);

        WorkBook
                workBook20 = addLesson(db,20,"Iyalama ngesi","iyalama_nyesi","When they will be grateful ","Thank you so much",
                "She was so grateful","They will be so grateful","She or He is thankful or grateful","She or He is thankful or grateful",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",3,1);

        WorkBook
                workBook21 = addLesson(db,21,"Eong itelekaarit","eong_itelekaarit","A state of gratification","The pleasure is his",
                "It's a pleasure","The pleasure is mine","The pleasure is hers","The pleasure is mine",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",3,1);

        WorkBook
                workBook22 = addLesson(db,22,"Mam ilimuni","mam_ilimuni","ssssshhhhhhh","Don’t mention it",
                "Keep quiet","Don't tell them","No problem","Don’t mention it",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",3,1);

        WorkBook
                workBook23 = addLesson(db,23,"Mam ipodo","mam_ipodo","It's okay","Don’t mind",
                "Never mind","Don't bother","It's okay","Never mind",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",3,1);

        /**
         *
         * End of Expressing Gratitude - basics
         * */

        /**
         *
         * start of Days - calendar
         * */

        WorkBook
                workBook24 = addLesson(db,24,"Lolo nabalasa","nabalasa","2nd day of the week","The day is Monday",
                "Today is Monday","Monday is today","Monday","Today is Monday",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",5,12);

        WorkBook
                workBook25 = addLesson(db,25,"Ejai esokoni namukaga","namukaga","Market day is on ...","Shopping is on Saturday",
                "The market day is on Saturday","Saturday is the day","Go to the market on Saturday","The market day is on Saturday",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",5,12);

        WorkBook
                workBook26 = addLesson(db,26,"Iwalari naiuniet","naiuniet"," ","The next day is Wednesday",
                "After its Wednesday","Wednesday","Wednesday is the day","The next day is Wednesday",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",5,12);

        WorkBook
                workBook27 = addLesson(db,27,"Ilipete etunga nasabiiti","nasabiiti"," ","Pray on Sunday",
                "People pray on Saturday","People pray on Sunday","Sunday is for prayers","People pray on Sunday",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",5,12);

        WorkBook
                workBook28 = addLesson(db,28,"Ebogunete eduwe naiwongonet","naiwongonet"," ","The children will come back on Thursday",
                "They will come back on Thursday","The children will come back on Wednesday","Thursday is when they come back","The children will come back on Thursday",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",5,12);

        WorkBook
                workBook29 = addLesson(db,29,"Elosi ngesi naikanyet","naikanyet"," ","They are going on Friday",
                "They will go on Friday","She or He isn't going on Friday","She or He is going on Friday","She or He is going on Friday",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",5,12);

        WorkBook
                workBook30 = addLesson(db,30,"Moyi nayareit","nayareit"," ","Tuesday is tomorrow",
                "Tomorrow is Tuesday","Tomorrow is not Tuesday","Tomorrow is the day","Tomorrow is Tuesday",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",5,12);


        /**
         *
         * End of Days - calendar
         * */

        /**
         *
         * start of Months - calendar
         * */

        WorkBook
                workBook31 = addLesson(db,31,"Iraraite ikito akwi duc olap lo Orara","orara",
                " ",
                "January",
                "Trees shed off leaves in February","Trees have leaves in January",
                "Trees always shed off leaves in January","Trees always shed off leaves in January",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",6,12);

        WorkBook
                workBook32 = addLesson(db,32,"Imukete itolumo duc olap lo Omuk","omuk",
                " ",
                "In February trees have shades",
                "Trees always develop shades in February","Trees have leaves in February",
                "Trees always shed off leaves in February","Trees always develop shades in February",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",6,12);

        WorkBook
                workBook33 = addLesson(db,33,"Ebaritos itunga duc olap lo Otibar","omuk",
                " ",
                "A lot of riches in September",
                "A lot from harvest in September","Always harvest in September",
                "A lot of riches always from harvest in September","A lot of riches always from harvest in September",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",6,12);


        /**
         *
         * end of Months - calendar
         * */

        /*
        WorkBook workBook13 = addLesson(db,13,"Yoga","yoga",null,null,
                null,null,null,"travel","weekdays",
                "vehicle","travel","numbers","ebalasa",
                "car","yoga","who","who",null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "picture_quiz",1,1);
        WorkBook workBook14 = addLesson(db,14,"Emotoka","car",null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,"Car",null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "voice_recording",1,1);
        */

        //calendar
        //days
        Phrase phrase1 = addPhrase(db,1,"Lolo nabalasa","nabalasa","Today is Monday.",0,5,12);
        Phrase phrase2 = addPhrase(db,2,"Moyi nayareit","nayareit","Tomorrow is Tuesday.",0,5,12);
        Phrase phrase3 = addPhrase(db,3,"Iwalari naiuniet","naiuniet","The next day is Wednesday",0,5,12);
        Phrase phrase4 = addPhrase(db,4,"Ebogunete eduwe naiwongonet","naiwongonet","The children will come back on Thursday.",0,5,12);
        Phrase phrase5 = addPhrase(db,5,"Elosi ngesi naikanyet","naikanyet","She or He is going on Friday.",0,5,12);
        Phrase phrase6 = addPhrase(db,6,"Ejai esokoni namukaga","namukaga","The market day is on Saturday.",0,5,12);
        Phrase phrase7 = addPhrase(db,7,"Ilipete etunga nasabiiti","nasabiiti","People pray on Sunday.",0,5,12);

        //calendar
        //months
        Phrase phrase8 = addPhrase(db,8,"Iraraite ikito akwi duc olap lo Orara","orara","Trees always shed off leaves in January",0,6,12);
        Phrase phrase9 = addPhrase(db,9,"Imukete itolumo duc olap lo Omuk","omuk","Trees always develop shades in February",0,6,12);
        Phrase phrase10 = addPhrase(db,10,"Ekwangitos itunga duc olap lo Okwang","okwang","People are always dirty preparing gardens in March",0,6,12);
        Phrase phrase11 = addPhrase(db,11,"Epol edou duc olap lo Odunge","odunge","The rains are always too heavy in April",0,6,12);
        Phrase phrase12 = addPhrase(db,12,"Ededeng etenge duc olap lo Opedelei","opedelei","There is always shortage of food in May",0,6,12);
        Phrase phrase13 = addPhrase(db,13,"Epol ebale duc olap lo Omaruk","omaruk","There are always mushrooms in June",0,6,12);
        Phrase phrase14 = addPhrase(db,14,"Ipu inyamat duc olap lo Omodokingol","omodokingol","There is always a lot of food in July",0,6,12);
        Phrase phrase15 = addPhrase(db,15,"Imokosi itunga duc olap lo Otikoi","otikoi","People are always satisfied in August",0,6,12);
        Phrase phrase16 = addPhrase(db,16,"Ebaritos itunga duc olap lo Otibar","otibar","A lot of riches always from harvest in September",0,6,12);
        Phrase phrase18 = addPhrase(db,18,"Ipu ipucito duc olap lo Osuban","osuban","Always a lot of functions in November",0,6,12);

        Phrase phrase20 = addPhrase(db,20,"Ijai biai?","ijai_biai","How are you?",0,1,1);
        Phrase phrase21 = addPhrase(db,21,"Ikwenyunit biai","ikwenyunit_biai","Good morning",0,1,1);
        Phrase phrase22 = addPhrase(db,22,"Yoga","yoga","Hello or Hi",0,1,1);
        Phrase phrase23 = addPhrase(db,23,"Ijaasi biai?","ijaasi_biai","How are you?(to many)",0,1,1);
        Phrase phrase24 = addPhrase(db,24,"Ejokuna, arai ijo?","ejokuna_arai_ijo","I am fine, how are you?",0,1,1);
        Phrase phrase25 = addPhrase(db,25,"Ejokuna, arai yesu?","ejokuna_arai_yesu","I am fine, how are you?(responding to many)",0,1,1);
        Phrase phrase26 = addPhrase(db,26,"Ekakiror erai...","ekakiror_erai","My name is...",0,1,1);
        Phrase phrase27 = addPhrase(db,27,"Ingai bo ekon kiror?","ingai_bo_ekon_kiror","What is your name?",0,1,1);
        Phrase phrase28 = addPhrase(db,28,"Eyalama airiamun","eyalama_ariamun","Pleased to meet you",0,1,1);
        Phrase phrase29 = addPhrase(db,29,"Eyalama awanyun","eyalama_awanyun","Pleased to see you",0,1,1);
        Phrase phrase30 = addPhrase(db,30,"Iboikin ber","iboikin_ber","Please sit",0,1,1);
        Phrase phrase31 = addPhrase(db,31,"Yoga do","yoga_do","Good bye",0,1,1);
        Phrase phrase32 = addPhrase(db,32,"Ojotor ojok","ojotor_ojok","Goodnight",0,1,1);

        //Expressing gratitude start
        Phrase phrase33 = addPhrase(db,33,"Eyalama","eyalama","Thank you",0,3,1);
        Phrase phrase35 = addPhrase(db,35,"Eyalamikini eong noi","eyalamikini_eong_noi","I will be so grateful",0,3,1);
        Phrase phrase36 = addPhrase(db,36, "Iyalama ngesi","iyalama_nyesi","She or He is thankful or grateful",0,3,1);
        Phrase phrase37 = addPhrase(db,37,"Iyalama ngesi noi","iyalama_nyesi_noi","He or She will be thankful or grateful",0,3,1);
        Phrase phrase38 = addPhrase(db,38,"Iyalamikin ngesi noi","iyalamikin_nyesi_noi","She or He will be so grateful or thankful",0,3,1);
        Phrase phrase40 = addPhrase(db,40,"Eong itelekaarit","eong_itelekaarit","The pleasure is mine",0,3,1);
        Phrase phrase41 = addPhrase(db,41,"Mam ilimuni","mam_ilimuni","Don’t mention it",0,3,1);
        Phrase phrase42 = addPhrase(db,42,"Mam ipodo","mam_ipodo","Never mind",0,3,1);
        Phrase phrase43 = addPhrase(db,43,"Mam ipodosi","mam_ipodosi","Never mind(to many)",0,3,1);

        //Travel
        Phrase phrase44 = addPhrase(db,44,"Akoto eong alosit Kampala","akoto_eong_alosit_kampala","I want to go to Kampala?",0,7,13);
        Phrase phrase45 = addPhrase(db,45,"Aibo ijai jo?","aibo_ijai_jo","Where are you?",0,7,13);
        Phrase phrase46 = addPhrase(db,46,"Aibo ejai eisaawe lo idegei?","aibo_ejai_eisaawe_lo_idegei","Where is the airport?",0,7,13);
        Phrase phrase47 = addPhrase(db,47,"Okwe odumaki eong abaasi","okwe_odumaki_eong_abaasi","Please get me a bus",0,7,13);
        Phrase phrase48 = addPhrase(db,48,"Aibo ne?","aibo_ne","Where is this?",0,7,13);
        Phrase phrase49 = addPhrase(db,49,"Olot ngaren cut mam apar","olot_ngaren_cut_mam_apar","Walk straight ahead",0,7,13);
        Phrase phrase50 = addPhrase(db,50,"Okudo teten","okudo_teten","Turn right",0,7,13);
        Phrase phrase51 = addPhrase(db,51,"Kediany","kediany","Left",0,7,13);
        Phrase phrase52 = addPhrase(db,52,"Ingarakinai eong itodik ne dokunes","ingarakinai_eong_itodik_ne_dokunes","Help tell me where to get off",0,7,13);
        Phrase phrase53 = addPhrase(db,53,"Esawa bani enyouna abaasi?","esawa_bani_enyouna_abaasi","What time does the bus leave?",0,7,13);
        Phrase phrase54 = addPhrase(db,54,"Elosi ngesi Nyalakakimat.","elosi_nyesi_nyalakakimat","He or She is going South.",0,7,13);
        Phrase phrase55 = addPhrase(db,55,"Nyakoi alomutor eong","nyakoi_alomutor_eong","North is where I come from.",0,7,13);
        Phrase phrase56 = addPhrase(db,56,"Ijesari akolong To.","ijesari_akolong_to","The sun sets in the West.",0,7,13);
        Phrase phrase57 = addPhrase(db,57,"Kide elomuna akolong","kide_elomuna_akolong","The sun rises from the East.",0,7,13);

        //Relations
        Phrase phrase58 = addPhrase(db,58,"Erai Akello acen ka.","erai_akello_acen_ka","Akello is my niece.",0,8,7);
        Phrase phrase59 = addPhrase(db,59,"Ocen ka ngon.","ocen_ka_ngon","That is my nephew",0,8,7);
        Phrase phrase61 = addPhrase(db,61,"Ebunit inacika.","ebunit_inac_ka","My sister is coming.",0,8,7);
        Phrase phrase62 = addPhrase(db,62,"Esiomi opajan ka","esiomi_opajan_ka","My cousin studies.",0,8,7);
        Phrase phrase63 = addPhrase(db,63,"Ingai ekiror apapa kon?","ingai_ekiror_apapa_kon","What is your fathers name?",0,8,7);
        Phrase phrase64 = addPhrase(db,64,"Ingai ekiror atoto kon?","ingai_ekiror_atoto_kon","What is your mother's name",0,8,7);
        Phrase phrase65 = addPhrase(db,65,"Papa ka ngesi je","papa_ka_ngesi_je","That is my grandfather",0,8,7);
        Phrase phrase66 = addPhrase(db,66,"Ejai tata ka musiri","ejai_tata_ka_musiri","My grandmother is in the garden",0,8,7);
        Phrase phrase67 = addPhrase(db,67,"Ija ka ngesi ngin","ija_ka_ngesi_ngin","That is my aunty",0,8,7);
        Phrase phrase68 = addPhrase(db,68,"Mamai ka ngesi Ochole","mamai_ka_ngesi_ochole","Ochole is my auncle",0,8,7);
        Phrase phrase69 = addPhrase(db,69,"Inac ka ngesi Akiror","inac_ka_ngesi_akiror", "Akiror is my sister",0,8,7);
        Phrase phrase70 = addPhrase(db,70,"Onac ka ngon","onac_ka_ngon","That is my brother",0,8,7);
        Phrase phrase71 = addPhrase(db,71,"Erasi ngun ika nacan","erasi_ngun_ika_nacan","Those are my siblings",0,8,7);
        Phrase phrase72 = addPhrase(db,72,"Anacan ngun","anacan_ngun","Those are sisters",0,8,7);
        Phrase phrase73 = addPhrase(db,73,"Imukeru bo angai ngin","imukeru_bo_angai_ngin","Whose baby is that?",0,8,7);
        Phrase phrase74 = addPhrase(db,74,"Ekoto ikoku alosit osomero","ekoto_ikoku_alosit_osomero","The child wants to go to school",0,8,7);
        Phrase phrase75 = addPhrase(db,75,"Iduwe ke Ikwap lu kere","iduwe_ke_ikwap_lu_kere","These are all Ikwap's children",0,8,7);
        Phrase phrase76 = addPhrase(db,76,"Apajan ka ngesi","apajan_ka_ngesi","She or he is my relative",0,8,7);
        Phrase phrase77 = addPhrase(db,77,"Ejai engo aberu ore","ejai_eong_aberu_ore","I have a wife at home",0,8,7);
        Phrase phrase78 = addPhrase(db,78,"Okilen ka ngon","okilen_ka_ngon","That is my husband",0,8,7);

        //Body parts
        Phrase phrase79 = addPhrase(db,79,"Ewojak ike tim","ewojak_ike_tim","Her or His hair is long",0,9,4);
        Phrase phrase80 = addPhrase(db,80,"Ekepitai eke reet","ekepitai_eke_reet","His or Her forehead was trimmed",0,9,4);
        Phrase phrase81 = addPhrase(db,81,"Epolok Joshua akonye","epolok_joshua_akonye","Joshua's eyes are big.",0,9,4);
        Phrase phrase82 = addPhrase(db,82,"Ewiyete apesse ikumes","ewiyete_apesse_ikumes","The girls nose is bleeding",0,9,4);
        Phrase phrase83 = addPhrase(db,83,"Akituk inerare","akituk_inerare","The mouth is for talking.",0,9,4);
        Phrase phrase84 = addPhrase(db,84,"Aki epupere","aki_epupere","The ears are for listening",0,9,4);
        Phrase phrase85 = addPhrase(db,85,"Ebut ya amatenget","ebut_ya_amatenget","Her cheek is swollen",0,9,4);
        Phrase phrase86 = addPhrase(db,86,"Ipii ya emukule","ipii_ya_emukule","Her skin itches",0,9,4);
        Phrase phrase87 = addPhrase(db,87,"Akulu eburit adam.","akulu_eburit_adam","The skull covers the brain",0,9,4);
        Phrase phrase88 = addPhrase(db,88,"Ejai akou kuju omorosin","ejai_akou_kuju_omorosin","The head is on top of the neck",0,9,4);
        Phrase phrase89 = addPhrase(db,89,"Adam ngesi ewomomei","adam_ngesi_ewomomei", "The brain is the one that thinks",0,9,4);
        Phrase phrase90 = addPhrase(db,90,"Enonok angajep","enonok_angajep","The tongue is soft",0,9,4);
        Phrase phrase91 = addPhrase(db,91,"Eyii angirit ke","eyii_angirit_ke","His or Her gum is bleeding",0,9,4);
        Phrase phrase92 = addPhrase(db,92,"Ekwakang Ikela ikoku","ekwakang_ikela_ikoku","The baby's teeth are white.",0,9,4);
        Phrase phrase94 = addPhrase(db,94,"Irioko ya icop","irioko_ya_icop","Her pupil is black",0,9,4);
        Phrase phrase95 = addPhrase(db,95,"Eriebi je ekeper.","eriebi_je_ekeper","His shoulder is paining.",0,9,4);
        Phrase phrase96 = addPhrase(db,96,"Ejasi ikiliok kede amasurubun","ejasi_ikiliok_kede_amasurubun","Men have beards",0,9,4);
        Phrase phrase97 = addPhrase(db,97,"Elayi eporoto abeeru angin","elayi_eporoto_abeeru_angin","That lady's voice is beautiful.",0,9,4);
        Phrase phrase99 = addPhrase(db,99,"Ewoja Okitoi egura","ewoja_okitoi_egura","Okitoi's spinal cord is long.",0,9,4);
        Phrase phrase100 = addPhrase(db,100,"Iwadik kede akan na oteten","iwadik_kede_akan_na_oteten","Write with the right hand.",0,9,4);
        Phrase phrase101 = addPhrase(db,101,"Epipil ake akan na okedyen","epipil_ake_akan_na_okedyen","His or her left hand is paining",0,9,4);
        Phrase phrase102 = addPhrase(db,102,"Ewuriaka ngesi ibookor","ewuriaka_ngesi_ibookor","His or Her fingers are short",0,9,4);
        Phrase phrase103 = addPhrase(db,103,"Ejai akirididi kwap akan","ejai_akirididi_kwap_akan", "Armpits are under the arm",0,9,4);
        Phrase phrase104 = addPhrase(db,104,"Ebilitai je amaraga","ebilitai_je_amaraga","His ribs were broken.",0,9,4);
        Phrase phrase105 = addPhrase(db,105,"Etakanete ngesi amori","etakanete_ngesi_amori","His or her veins are seen",0,9,4);
        Phrase phrase107 = addPhrase(db,107,"Ejai engo atorob na epol","ejai_engo_atorob_na_epol","I have a big chest.",0,9,4);
        Phrase phrase108 = addPhrase(db,108,"Erengak aokot","erengak_aokot","Blood is red",0,9,4);
        Phrase phrase109 = addPhrase(db,109,"Emunara eke emanyi","emunara_eke_manyi","His or her liver is spoilt",0,9,4);
        Phrase phrase110 = addPhrase(db,110,"Epol ingalur aswam","epol_ingalur_aswam","The kidneys have a lot of work.",0,9,4);
        Phrase phrase112 = addPhrase(db,112,"Elumorit ngesi aipul","elumorit_ngesi_aipul","His or Her navel is inside",0,9,4);
        Phrase phrase113 = addPhrase(db,113,"Epalal ngesi akanin","epalal_ngesi_akanin","His or Her palms are wet.",0,9,4);
        Phrase phrase115 = addPhrase(db,115,"Edit Akwi akoik","edit_akwi_akoik","Akwi's stomach is small",0,9,4);
        Phrase phrase120 = addPhrase(db,120,"Eriana ngesi epur","eriana_ngesi_epur","His or Her back is flat",0,9,4);
        Phrase phrase121 = addPhrase(db,121,"Iyenga oni kede iwukoi","iyenga_oni_kede_iwukoi","We breath through the lungs.",0,9,4);
        Phrase phrase122 = addPhrase(db,122,"Eriebi engo ececelu","eriebi_engo_ececelu","The groin is paining me.",0,9,4);
        Phrase phrase123 = addPhrase(db,123,"Epolok ya amuros","epolok_ya_amuros","Her thighs are big",0,9,4);
        Phrase phrase124 = addPhrase(db,124,"Ekukokina ya akungin ke","ekukokina_ya_akungin_ke","She knelt on her knees",0,9,4);
        Phrase phrase125 = addPhrase(db,125,"Egogong akojo","egogong_akojo","Bones are hard.",0,9,4);
        Phrase phrase126 = addPhrase(db,126,"Edisiak je akeje","edisiak_je_akeje","His legs are small",0,9,4);
        Phrase phrase127 = addPhrase(db,127,"Epipil engo ekurunyun","epipil_engo_ekurunyun","My kneecap is paining.",0,9,4);
        Phrase phrase129 = addPhrase(db,129,"Ejatator apesur isiepon","ejatator_apesur_isiepon","Girls have hips",0,9,4);
        Phrase phrase133 = addPhrase(db,133,"Elosete itunga kede akeje","elosete_itunga_kede_akeje","People walk on Foot",0,9,4);

        //clothing
        Phrase phrase136 = addPhrase(db,136, "Ecilil eiteteyi","ecilil_eiteteyi","The dress is torn.",0,1,5);
        Phrase phrase137 = addPhrase(db,137,"Erai ekoti ngon lo bululu","erai_ekoti_ngon_lo_bululu","That coat is blue.",0,10,5);
        Phrase phrase138 = addPhrase(db,138,"Itet epulani","itet_epulani","The vest is new.",0,1,5);
        Phrase phrase139 = addPhrase(db,139,"Ejai eke esati ocalo","ejai_eke_esati_ocalo","His shirt is in the village.",0,10,5);
        Phrase phrase141 = addPhrase(db,141,"Emojongit esiya","emojongit_esiya","The girl's beaded apron is old.",0,10,5);
        Phrase phrase142 = addPhrase(db,142, "Ebenen eimukes iduwe","ebenen_eimukes_iduwe", "The childrens blanket is light.",0,10,5);
        Phrase phrase143 = addPhrase(db,143,"Epalal edabada","epalal_edabada","The trouser is wet.",0,10,5);
        Phrase phrase144 = addPhrase(db,144,"Elotari akopira","elotari_akopira","The hat has been washed.",0,10,5);
        Phrase phrase145 = addPhrase(db,145,"Edolit apese alega.","edolit_apese_alega","The bra fits the girl.",0,10,5);
        Phrase phrase146 = addPhrase(db,146,"Enapit esapat asokosin","enapit_esapat_asokosin","The boy is putting on stockings.",0,10,5);
        Phrase phrase147 = addPhrase(db,147,"Atayin adi bo ngun?","atayin_adi_bo_ngun","How many ties are those?",0,10,5);
        Phrase phrase148 = addPhrase(db,148,"Ipu eong atelei","ipu_eong_atelei","I have many aprons.",0,10,5);
        Phrase phrase149 = addPhrase(db,149,"Adolu ngesi kede abwos a Toto.","adolu_ngesi_kede_abwos_a_toto","She or He has arrived with mother's leather skirt",0,10,5);
        Phrase phrase150 = addPhrase(db,150,"Enapit ngesi egomasi.","enapit_ngesi_egomasi","She is putting on a gomasi",0,10,5);
        Phrase phrase151 = addPhrase(db,151,"Iriono ekanzu","iriono_ekanzu","The kanzu is dirty.",0,10,5);
        Phrase phrase152 = addPhrase(db,152,"Adonyokin atambala","adonyokin_atambala","The handkerchief has been sewed.",0,10,5);

        //Numbers
        Phrase phrase153 = addPhrase(db,153,"Abakasa adiopet","abakasa_ediopet","One envelop",0,2,1);
        Phrase phrase154 = addPhrase(db,154,"Abaasin aare","abaasin_aare","Two buses",0,2,1);
        Phrase phrase155 = addPhrase(db,155,"Abikirian auni","abikirian_auni","Three catholic nuns",0,2,1);
        Phrase phrase156 = addPhrase(db,156,"Abeei aongon" ,"abeei_aongon", "Four eggs",0,2,1);
        Phrase phrase157 = addPhrase(db,157,"Abiolosin akany'ape","abiolosin_akanyape","Six ambulances",0,2,1);
        Phrase phrase158 = addPhrase(db,158,"Abiroi akany'aare","abiroi_akanyaare","Seven big sticks(club)",0,2,1);
        Phrase phrase159 = addPhrase(db,159,"Abukui akanyauni","abukui_akanyauni","Eight book covers",0,2,1);
        Phrase phrase160 = addPhrase(db,160,"Abubuk akany'aongon","abubuk_akanyaongon","Nine groundnut shells or husks",0,2,1);
        Phrase phrase161 = addPhrase(db,161,"Abuwai atomon","abuwai_atomon","Ten caves",0,2,1);
        Phrase phrase162 = addPhrase(db,162,"Abootin atomon'adiop","abootin_atomonadiop","Eleven votes",0,2,1);
        Phrase phrase163 = addPhrase(db,163,"Aboolei atomon aare","aboolei_atomonaare","Twelve cobs",0,2,1);
        Phrase phrase164 = addPhrase(db,164,"Abongun atomon auni","abongun_atomon_auni","Thirteen bark clothes",0,2,1);
        Phrase phrase165 = addPhrase(db,165,"Abulesin atomon aongon","abulesin_atomon_aongon","Fourteen maid servants",0,2,1);
        Phrase phrase166 = addPhrase(db,166,"Aburaasin atomon akany","aburaasin_atomon_akany","Fifteen brushes",0,2,1);
        Phrase phrase167 = addPhrase(db,167,"Aburacun atomon akany'ape","aburacun_atomon_akanyape","Sixteen clasps",0,2,1);
        Phrase phrase168 = addPhrase(db,168,"Abureta atomon akany'aare","abureta_atomon_akanyaare","Seventeen wrappers or covers",0,2,1);
        Phrase phrase169 = addPhrase(db,169,"Abutuusin atomon akany'auni","abutuusin_atomon_akanyauni","Eighteen rubber or jungle boots.",0,2,1);
        Phrase phrase170 = addPhrase(db,170,"abuuban atomon akany'aongon","abuuban_atomon_akanyaongon","Nineteen tyre hand pumps",0,2,1);
        Phrase phrase171 = addPhrase(db,171,"Abwaadei akais-aare","abwaadei_akaisaare","Twenty prostitutes",0,2,1);
        Phrase phrase172 = addPhrase(db,172,"Abwacin akais-aare en diop","abwacin_akaisaare_endiop","Twenty one barren women",0,2,1);
        Phrase phrase173 = addPhrase(db,173,"Abwakan akais-aare alu aare","abwakan_akaisaare_alu_aare","Twenty two ear rings",0,2,1);
        Phrase phrase174 = addPhrase(db,174,"Ibaibulin akais-aare alu uni","ibaibulin_akaisaare_alu_uni","Twenty three bibles",0,2,1);
        Phrase phrase175 = addPhrase(db,175,"Ibaluan akais-aare alu wongon","ibaluan_akaisaare_alu_wongon","Twenty Four letters",0,2,1);
        Phrase phrase176 = addPhrase(db,176,"Ibajetin akais-aare alu kany","ibajetin_akaisaare_alu_kany","Twenty Five budgets",0,2,1);
        Phrase phrase177 = addPhrase(db,177,"Iboyan akais-aare alu kany'ape","iboyan_akaisaare_alu_kanyape","Twenty Six fish nets",0,2,1);
        Phrase phrase178 = addPhrase(db,178,"Icoloonin akais-aare alu kany'aare","icoloonin_akaisaare_alu_kanyaare","Twenty Seven pit latrines",0,2,1);
        Phrase phrase179 = addPhrase(db,179,"Ibirinyaanyan akais-aare alu kany'auni","ibirinyaanyan_akaisaare_alu_kanyauni","Twenty Eight egg plants",0,2,1);
        Phrase phrase180 = addPhrase(db,180,"Idiigai akais-aare alu kany'aongon","idiigai_akaisaare_alu_kanyaongon","Twenty Nine male sheep",0,2,1);
        Phrase phrase181 = addPhrase(db,181,"idoboi akais-auni","idoboi_akaisauni","Thirty big fishing hooks",0,2,1);
        Phrase phrase182 = addPhrase(db,182,"Iguudoi akais-auni en diop","iguudoi_akaisauni_en_diop","Thirty one roads",0,2,1);
        Phrase phrase183 = addPhrase(db,183,"Aditai akais-auni alu aare","aditai_akaisauni_alu_aare","Thirty two baskets",0,2,1);
        Phrase phrase184 = addPhrase(db,184,"Ayinakineta akais-auni alu uni","ayinakineta_akaisauni_alu_uni","Thirty three gifts",0,2,1);
        Phrase phrase185 = addPhrase(db,185,"Ibokor akais-auni alu wongon","ibokor_akaisauni_alu_wongon","Thirty four index fingers",0,2,1);
        Phrase phrase186 = addPhrase(db,186,"Eeburak akais-auni alu kany","eeburak_akaisauni_alu_kany","Thirty five debt collectors",0,2,1);
        Phrase phrase187 = addPhrase(db,187,"Idukurun akais-auni alu kany'ape","idukurun_akaisauni_alu_kanyape","Thirty six small huts",0,2,1);
        Phrase phrase188 = addPhrase(db,188,"Idonyisyo akais-auni alu kany'aare","idonyisyo_akaisauni_alu_kanyaare","Thirty seven sewing needles",0,2,1);
        Phrase phrase189 = addPhrase(db,189,"Igaalin akais-auni alu kany'auni","igaalin_akaisauni_alu_kanyauni","Thirty eight bicycles",0,2,1);
        Phrase phrase190 = addPhrase(db,190,"Ijeran akais-auni alu kany'aongon","ijeran_akaisauni_alu_kanyaongon","Thirty nine prisioners",0,2,1);
        Phrase phrase191 = addPhrase(db,191,"Eejaanakinak akais-aongon","eejaanakinak_akaisaongon","Forty servants or attendants",0,2,1);
        Phrase phrase192 = addPhrase(db,192,"Imaradadin akais-aongon en diop","imaradadin_akaisaongon_en_diop","Forty one decorations",0,2,1);
        Phrase phrase193 = addPhrase(db,193,"Iipoisyo akais-aongon alu aare","iipoisyo_akaisaongon_alu_aare","Forty two kitchens",0,2,1);
        Phrase phrase194 = addPhrase(db,194,"Ijiiso akais-aongon alu iuni","ijiiso_akaisaongon_alu_uni","Forty three weapons",0,2,1);
        Phrase phrase195 = addPhrase(db,195,"Ikadukok akais-aongon alu wongon","ikadukok_akaisaongon_alu_wongon","Fouty four builders",0,2,1);
        Phrase phrase196 = addPhrase(db,196,"Ikalumok akais-aongon alu kany","ikalumok_akaisaongon_alu_kany","Forty five swimmers",0,2,1);
        Phrase phrase197 = addPhrase(db,197,"Ikaru akais-aongon alu kany'ape","ikaru_akaisaongon_alu_kanyape","Forty six years",0,2,1);
        Phrase phrase198 = addPhrase(db,198,"Ikekia akais-aongon alu kany'aare","ikekia_akaisaongon_alu_kanyaare","Forty seven doors",0,2,1);
        Phrase phrase199 = addPhrase(db,199,"Ikebwokok akais-aongon alu kany'auni","ikebwokok_akaisaongon_alu_kanyauni","Forty eight porters",0,2,1);
        Phrase phrase200 = addPhrase(db,200,"Imieseko akais-aongon alu kany'aongon","imieseko_akaisaongon_alu_kanyaongon","Forty nine razor blades",0,2,1);
        Phrase phrase201 = addPhrase(db,201,"Ikapukok akais-akany","ikapukok_akaisakany","Fifty listeners or hearers",0,2,1);
        Phrase phrase202 = addPhrase(db,202,"Ikemerak akais-akany en diop","ikemerak_akaisakany_en_diop","Fifty one albinos",0,2,1);
        Phrase phrase204 = addPhrase(db,204,"Ikaarak akais-akany alu uni","ikaarak_akaisakany_alu_uni","Fifty three serial killers",0,2,1);
        Phrase phrase205 = addPhrase(db,205,"Ikiosikin akais-akany alu wongon","ikiosikin_akaisakany_alu_wongon","Fifty four kiosks",0,2,1);
        Phrase phrase206 = addPhrase(db,206,"Ikiliok akais-akany alu kany","ikiliok_akaisakany_alu_kany","Fifty five men",0,2,1);
        Phrase phrase207 = addPhrase(db,207,"Awojak akais-akany alu ikany'ape","awojak_akaisakany_alu_ikanyape","Fifty six tall people",0,2,1);
        Phrase phrase208 = addPhrase(db,208,"lukiyai akais-akany alu kany'aare","lukiyai_akaisakany_alu_kanyaare","Fifty seven first born boys",0,2,1);
        Phrase phrase209 = addPhrase(db,209,"Ikweei akais-akany alu kany'auni","ikweei_akaisakany_alu_kanyauni","Fifty eight foxes",0,2,1);
        Phrase phrase210 = addPhrase(db,210,"Amerak akais-akany alu kany'aongon","amerak_akaisakany_alu_kanyaongon","Fifty nine drunkards",0,2,1);
        Phrase phrase211 = addPhrase(db,211,"Imienai akais-akany'ape","imienai_akaisakanyape","Sixty bats",0,2,1);
        Phrase phrase212 = addPhrase(db,212,"Asujon akais-akany'ape en diop","asujon_akaisakanyape_en_diop","Sixty one pumpkins",0,2,1);
        Phrase phrase213 = addPhrase(db,213,"Amor akais-akany'ape alu aare","amor_akaisakanyape_alu_aare","Sixty two stones",0,2,1);
        Phrase phrase214 = addPhrase(db,214,"Imopiiran akais-akany'ape alu uni","imopiiran_akaisakanyape_alu_uni","Sixty three balls",0,2,1);
        Phrase phrase215 = addPhrase(db,215,"Imunwo akais-akany'ape alu wongon","imunwo_akaisakanyape_alu_wongon","Sixty four snakes",0,2,1);
        Phrase phrase216 = addPhrase(db,216,"Imusalaban akais-akany'ape alu kany","imusalaban_akaisakanyape_alu_kany","Sixty five crosses",0,2,1);
        Phrase phrase217 = addPhrase(db,217,"Inaabin akais-akany'ape alu ikany'aape","inaabin_akaisakanyape_alu_ikanyape","Sixty six prophets",0,2,1);
        Phrase phrase218 = addPhrase(db,218,"Imwalimun akais-akany'ape alu kany'aare","imwalimun_akaisakanyape_alu_kanyaare","Sixty seven teachers",0,2,1);


    }

    //AsyncTask that populates the database
    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void>{

        private final AtesoDatabase atesoDatabase;

        PopulateDbAsync(AtesoDatabase db){
            atesoDatabase = db;
        }

        @Override
        protected Void doInBackground(final Void... params){

            populateWithTestData(atesoDatabase);
            Log.d(TAG, "Database content inserted");

            return null;
        }
    }
}
