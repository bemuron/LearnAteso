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
        WorkBook.class, Phrase.class}, version = 3, exportSchema = false)
public abstract class AtesoDatabase extends RoomDatabase {
    private static final String TAG = AtesoDatabase.class.getSimpleName();

    public abstract CategoriesDao categoriesDao();
    public abstract UsersDao usersDao();
    public abstract WorkBookDao workBookDao();
    public abstract SectionsDao sectionsDao();
    public abstract PhrasesDao phrasesDao();
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
                            .addMigrations(MIGRATION_2_3)
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
    public static final Migration MIGRATION_1_2 = new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //since we didnt alter the table, there's nothing else to do here
        }
    };

    //migrating from db v2 to v3
    //rename ateso_phrase and transaltion tables to suggest_text_1 and suggest_text_2 as required
    //for the search to work well
    //create new column phrase_pic
    public static final Migration MIGRATION_2_3 = new Migration(2,3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //create new virtual table for phrases
            database.execSQL("CREATE VIRTUAL TABLE phrases_new USING FTS3" +
                    "(rowid, suggest_text_1, audio, suggest_text_2," +
                    " isFavourite, phrase_pic," +
                    " section_id, category_id)");

            //copy the data
            database.execSQL("INSERT INTO phrases_new (rowid, suggest_text_1, audio, suggest_text_2," +
                    "isFavourite, section_id, category_id) SELECT phrase_id, ateso_phrase, " +
                    "audio, translation, isFavourite, section_id, category_id FROM phrases");

            //remove the old phrases table
            database.execSQL("DROP TABLE phrases");

            //rename the table to the correct name
            database.execSQL("ALTER TABLE phrases_new RENAME TO phrases");

            //add the new column
            //database.execSQL("ALTER TABLE phrases ADD COLUMN phrase_pic TEXT");
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
                                    String audio, String translation, String phrasePic, int isFavourite,
                                    int sectionId, int categoryId){
        Phrase atesoPhrase = new Phrase();
        atesoPhrase.setRowId(phraseId);
        atesoPhrase.setAtesoPhrase(phrase);
        atesoPhrase.setAtesoAudio(audio);
        atesoPhrase.setTranslation(translation);
        atesoPhrase.setPhrasePic(phrasePic);
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
        Section section11 = addSection(db, 11, "Weather", "weather", 13);
        Section section12 = addSection(db, 12, "Clock", "clock", 12);

        Category category1 = addCategory(db, 1, "Basics", "basics");
        //Category category2 = addCategory(db, 2, "Food", "food");
        //Category category3 = addCategory(db, 3, "Animals", "animals");
        Category category4 = addCategory(db, 4, "Human Body", "bodyparts");
        Category category5 = addCategory(db, 5, "Clothing", "clothing");
        Category category7 = addCategory(db, 7, "Relations", "relations");
        Category category12 = addCategory(db, 12, "Time", "time");
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
                workBook33 = addLesson(db,33,"Ebaritos itunga duc olap lo Otibar","otibar",
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

        WorkBook workBook96 = addLesson(db,96,"Ipu ipucito duc olap lo Osuban",
                "osuban"," ",null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                "Always a lot of functions in November","Ipu ipucito duc olap lo Osuban",
                "sentence_construction",6,12);

        WorkBook workBook97 = addLesson(db,97,"Imokosi itunga duc olap lo Otikoi",
                "otikoi"," ",null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                "People are always satisfied in August","Imokosi itunga duc olap lo Otikoi",
                "sentence_construction",6,12);

        WorkBook workBook98 = addLesson(db,98,"Epol edou duc olap lo Odunge","odunge",
                " ",
                "A lot of riches in September",
                "A lot from harvest in September","The rains are always too heavy in May",
                "The rains are always too heavy in April","The rains are always too heavy in April",
                null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",6,12);

        WorkBook workBook99 = addLesson(db,99,"Ekwangitos itunga duc olap lo Okwang",
                "okwang"," ",null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                "People are always dirty preparing gardens in March","Ekwangitos itunga duc olap lo Okwang",
                "sentence_construction",6,12);

        WorkBook workBook100 = addLesson(db,100,"Ededeng etenge duc olap lo Opedelei",
                "opedelei"," ",null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                "There is always shortage of food in May",
                "Ededeng etenge duc olap lo Opedelei",
                "sentence_construction",6,12);


        /**
         *
         * end of Months - calendar
         * */

        /**
         *
         * start of Body Parts - Human Body
         * */

        WorkBook
                workBook34 = addLesson(db,34,"Ewiyete apesse ikumes","ewiyete_apesse_ikumes",
                " ",
                "The girl's nose is bleeding",
                "The nose is bleeding","The boy's nose is bleeding",
                "The woman's nose is bleeding","The girl's nose is bleeding",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",9,4);

        WorkBook
                workBook35 = addLesson(db,35,"Akituk inerare","akituk_inerare",
                "For talking",
                "My mouth is for talking",
                "The mouth is mine","The mouth is for talking",
                "I am talking","The mouth is for talking",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",9,4);

        WorkBook
                workBook36 = addLesson(db,36,"Ebut ya amatenget","ebut_ya_amatenget",
                " ",
                "Open the boot",
                "Her cheek is swollen","Her cheek is big",
                "My cheek is swollen","Her cheek is swollen",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",9,4);

        WorkBook
                workBook37 = addLesson(db,37,"Akulu eburit adam","akulu_eburit_adam",
                " ",
                "The brain is on top",
                "Akulu is bigger than Adam","Adam has a skull",
                "The skull covers the brain","The skull covers the brain",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",9,4);

        WorkBook
                workBook38 = addLesson(db,38,"Enonok angajep","enonok_angajep",
                " ",
                "The language is soft",
                "The mouth is soft","The tongue is soft",
                "The lips are big","The tongue is soft",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",9,4);

        WorkBook
                workBook39 = addLesson(db,39,"Ekwakang ikela ikoku","ekwakang_ikela_ikoku",
                " ",
                "The teeth are white",
                "The baby's teeth are white","His teeth are brown",
                "The baby's mouth is small","The baby's teeth are white",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",9,4);

        WorkBook
                workBook40 = addLesson(db,40,"Eriebi je ekeper","eriebi_je_ekeper",
                " ",
                "His shoulder is paining",
                "His arm is hurting","My shoulder is paining",
                "Her stomach is hurting","His shoulder is paining",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",9,4);

        WorkBook workBook41 = addLesson(db,41,"Elayi eporoto abeeru angin",
                "elayi_eporoto_abeeru_angin","beautiful voice",null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                "That lady's voice is beautiful","Elayi eporoto abeeru angin",
                "sentence_construction",9,4);

        WorkBook workBook42 = addLesson(db,42,"Iwadik kede akan na oteten",
                "iwadik_kede_akan_na_oteten","hand writing",null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                "Write with the right hand","Iwadik kede akan na oteten",
                "sentence_construction",9,4);

        WorkBook workBook43 = addLesson(db,43,"Ewuriaka ngesi ibookor",
                "ewuriaka_ngesi_ibookor"," ",null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                "His or Her fingers are short","Ewuriaka ngesi ibookor",
                "sentence_construction",9,4);

        WorkBook
                workBook44 = addLesson(db,44,"Elosete itunga kede akeje","elosete_itunga_kede_akeje",
                " ",
                "The people are going",
                "People walk on foot","The feet are for walking",
                "People use the feet","People walk on foot",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",9,4);

        WorkBook workBook45 = addLesson(db,45,"Erengak aokot",
                "erengak_aokot","blood red",null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                "Blood is red","Erengak aokot",
                "sentence_construction",9,4);

        /**
         *
         * end of Body Parts - Human Body
         * */

        /**
         *
         * Start of Wardrobe - Clothing
         * */

        WorkBook workBook46 = addLesson(db,46,"Ecilil eiteteyi",
                "ecilil_eiteteyi"," ",null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                "The dress is torn","Ecilil eiteteyi",
                "sentence_construction",10,5);

        WorkBook
                workBook47 = addLesson(db,47,"Adonyokin atambala","adonyokin_atambala",
                " ",
                "I have a handkerchief",
                "The cloth has been sewed","The handkerchief has been sewed",
                "The blanket has been sewed","The handkerchief has been sewed",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",10,5);

        WorkBook
                workBook48 = addLesson(db,48,"Iriono ekanzu","iriono_ekanzu",
                " ",
                "The cloth is dirty",
                "The shirt is dirty","The kanzu is black",
                "The kanzu is dirty","The kanzu is dirty",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",10,5);

        WorkBook workBook49 = addLesson(db,49,"Erai ekoti ngon lo bululu",
                "erai_ekoti_ngon_lo_bululu"," ",null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                "That coat is blue","Erai ekoti ngon lo bululu",
                "sentence_construction",10,5);

        WorkBook
                workBook50 = addLesson(db,50,"Itet epulani","itet_epulani",
                " ",
                "The plan is new",
                "The vest is new","My vest is new",
                "The trouser is new","The vest is new",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",10,5);

        WorkBook workBook51 = addLesson(db,51,"Erai ekoti ngon lo bululu",
                "erai_ekoti_ngon_lo_bululu"," ",null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                "That coat is blue","Erai ekoti ngon lo bululu",
                "sentence_construction",10,5);

        WorkBook
                workBook52 = addLesson(db,52,"Elotari akopira","elotari_akopira",
                " ",
                "The hat has been washed",
                "The hat is clean","The shirt has been washed",
                "The hat is new","The hat has been washed",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",10,5);

        WorkBook workBook53 = addLesson(db,53,"Ejai eke esati ocalo",
                "ejai_eke_esati_ocalo"," ",null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                "His shirt is in the village","Ejai eke esati ocalo",
                "sentence_construction",10,5);


        /**
         *
         * end of Wardrobe - Clothing
         * */


        /**
         *
         * start of Family - Relations
         * */

        WorkBook workBook54 = addLesson(db,54,"Erai Akello acen ka",
                "erai_akello_acen_ka"," ",null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                "Akello is my niece","Erai Akello acen ka",
                "sentence_construction",8,7);

        WorkBook workBook55 = addLesson(db,55,"Ekoto ikoku alosit osomero",
                "ekoto_ikoku_alosit_osomero"," ",null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                "The child wants to go to school","Ekoto ikoku alosit osomero",
                "sentence_construction",8,7);

        WorkBook
                workBook56 = addLesson(db,56,"Ocen ka ngon","ocen_ka_ngon",
                " ",
                "He is my nephew",
                "My nephew is here","That is my nephew",
                "I have a nephew","That is my nephew",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",8,7);

        WorkBook workBook57 = addLesson(db,57,"Ingai ekiror apapa kon?",
                "ingai_ekiror_apapa_kon"," ",null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                "What is your fathers name?","Ingai ekiror apapa kon?",
                "sentence_construction",8,7);

        WorkBook
                workBook58 = addLesson(db,58,"Papa ka ngesi je","papa_ka_ngesi_je",
                " ",
                "He is my grandfather",
                "That is my grandfather","My grandfather is here",
                "Where is my grandfather?","That is my grandfather",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",8,7);

        WorkBook workBook59 = addLesson(db,59,"Ija ka ngesi ngin",
                "ija_ka_ngesi_ngin"," ",null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                "That is my aunt","Ija ka ngesi ngin",
                "sentence_construction",8,7);

        WorkBook
                workBook60 = addLesson(db,60,"Mamai ka ngesi Ochole","mamai_ka_ngesi_ochole",
                " ",
                "My mom is Ochole",
                "Where is my mom","Ochole is my uncle",
                "My name is Ochole","Ochole is my uncle",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",8,7);

        WorkBook
                workBook61 = addLesson(db,61,"Erasi ngun ika nacan","erasi_ngun_ika_nacan",
                " ",
                "Where are my siblings",
                "Those are my siblings","Those are my brothers",
                "Those are my sisters","Those are my siblings",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",8,7);

        WorkBook workBook62 = addLesson(db,62,"Imukeru bo angai ngin?",
                "imukeru_bo_angai_ngin"," ",null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                "Whose baby is that?","Ija ka ngesi ngin",
                "sentence_construction",8,7);

        WorkBook
                workBook63 = addLesson(db,63,"Ejai eong aberu ore","ejai_eong_aberu_ore",
                " ",
                "I have a wife at home",
                "I have a wives at home","My wife is at home",
                "At home with my wife","I have a wife at home",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",8,7);


        /**
         *
         * end of Family - Relations
         * */

        /**
         *
         * start of Directions - Travel
         * */

        WorkBook
                workBook64 = addLesson(db,64,"Kediany","kediany",
                " ",
                "Go left",
                "Left","Right",
                "Go right","Left",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",7,13);

        WorkBook
                workBook65 = addLesson(db,65,"Akoto eong alosit Kampala","akoto_eong_alosit_kampala",
                " ",
                "I am going to Kampala",
                "We are in Kampala","I want to go to Kampala",
                "I am in Kampala","I want to go to Kampala",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",7,13);

        WorkBook workBook66 = addLesson(db,66,"Aibo ijai jo?",
                "aibo_ijai_jo"," ",null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                "Where are you?","Aibo ijai jo?",
                "sentence_construction",7,13);

        WorkBook
                workBook67 = addLesson(db,67,"Aibo ejai eisaawe lo idegei?","aibo_ejai_eisaawe_lo_idegei",
                " ",
                "Where is the air plane?",
                "Is this the airport?","Is that an aeroplane?",
                "Where is the airport?","Where is the airport?",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",7,13);

        WorkBook workBook68 = addLesson(db,68,"Okwe odumaki eong abaasi",
                "okwe_odumaki_eong_abaasi"," ",null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                "Please get me a bus","Okwe odumaki eong abaasi",
                "sentence_construction",7,13);

        WorkBook
                workBook69 = addLesson(db,69,"Aibo ne?","aibo_ne",
                " ",
                "Where is this?",
                "Where are we?","What is this place?",
                "Where are you going?","Where is this?",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",7,13);

        WorkBook
                workBook70 = addLesson(db,70,"Okudo teten","okudo_teten",
                " ",
                "Turn left",
                "Go North","Turn Eastwards",
                "Turn right","Turn right",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",7,13);

        WorkBook workBook71 = addLesson(db,71,"Olot ngaren cut mam apar",
                "olot_ngaren_cut_mam_apar"," ",null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                "Walk straight ahead","Olot ngaren cut mam apar",
                "sentence_construction",7,13);

        WorkBook workBook72 = addLesson(db,72,"Ijesari akolong To",
                "ijesari_akolong_to"," ",null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                "The sun sets in the West","Ijesari akolong To",
                "sentence_construction",7,13);

        WorkBook workBook73 = addLesson(db,73,"Kide elomuna akolong","kide_elomuna_akolong",
                " ",
                "The sun is out",
                "The sun rises from the East","The sun rises from the West",
                "The sun is facing East","The sun rises from the East",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",7,13);

        WorkBook workBook74 = addLesson(db,74,"Esawa bani enyouna abaasi?","esawa_bani_enyouna_abaasi",
                " ",
                "What time does the bus leave?",
                "When does the bus leave?","Is the bus leaving?",
                "Has the bus left?","What time does the bus leave?",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",7,13);

        /**
         *
         * end of Directions - Travel
         * */

        /**
         *
         * Start of Weather - Travel
         * */

        WorkBook workBook75 = addLesson(db,75,"Ayapu akamu","ayapu_akamu",
                " ",
                "It's the dry season",
                "The wind is blowing","Rainy season is soon",
                "Dry season is soon","Dry season is soon",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",11,13);

        WorkBook workBook76 = addLesson(db,76,"Ededeng amwanis","ededeng_amwanis",
                " ",
                "The heat is strong",
                "It is not very hot","The heat is too much",
                "There is too much heat","The heat is too much",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",11,13);

        WorkBook workBook77 = addLesson(db,77,"Eroko anya edakitos akop",
                "eroko_anya_edakitos_akop"," ",null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                "The grass is still carrying dew","Eroko anya edakitos akop",
                "sentence_construction",11,13);

        WorkBook workBook78 = addLesson(db,78,"Ayapu aporo",
                "ayapu_aporo"," ",null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                "Wet season is soon","Ayapu aporo",
                "sentence_construction",11,13);

        WorkBook workBook79 = addLesson(db,79,"Acaou akolong","acaou_akolong",
                " ",
                "The sun is shining",
                "The sun is hot","The sun is not shining",
                "The rain is falling","The sun is shining",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",11,13);

        WorkBook workBook80 = addLesson(db,80,"Irikina etaluka","irikina_etaluka",
                " ",
                "The rainbow isn't across",
                "The clouds are across","The rainbow is across",
                "See the rainbow","The sun is shining",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",11,13);

        WorkBook workBook81 = addLesson(db,81,"Adiloki ekuna",
                "adiloki_ekuna"," ",null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                "The mist has started early","Adiloki ekuna",
                "sentence_construction",11,13);

        WorkBook workBook82 = addLesson(db,82,"Elai akuj","elai_akuj",
                " ",
                "The sky is big",
                "The clouds are beautiful","The sky is beautiful",
                "The sky is good","The sky is beautiful",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",11,13);

        WorkBook workBook83 = addLesson(db,83,"Ededeng alilim",
                "ededeng_alilim"," ",null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                "The weather is cold","Ededeng alilim",
                "sentence_construction",11,13);


        /**
         *
         * end of Weather - Travel
         * */

        /**
         *
         * Start of Clock - Time
         * */

        WorkBook workBook84 = addLesson(db,84,"Atikitikin atomon","atikitikin_atomon",
                " ",
                "Ten minutes",
                "Ten hours","Five seconds",
                "Ten seconds","Ten seconds",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",12,12);

        WorkBook workBook85 = addLesson(db,85,"Esawan iare","esawan_iare",
                " ",
                "Two o'clock",
                "Twelve o'clock","Eight o'clock",
                "Three o'clock","Eight o'clock",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",12,12);

        WorkBook workBook86 = addLesson(db,86,"Esawa ediopet atutubet",
                "esawa_ediopet_atutubet"," ",null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                "Thirty minutes past seven","Esawa ediopet atutubet",
                "sentence_construction",12,12);

        WorkBook workBook87 = addLesson(db,87,"Esawan itomon aare","esawan_itomon_aare",
                " ",
                "Twelve o'clock",
                "Two o'clock","Eight o'clock",
                "Three o'clock","Twelve o'clock",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",12,12);

        WorkBook workBook88 = addLesson(db,88,"Esawan iare atutubet","esawan_iare_atutubet",
                " ",
                "Thirty minutes past two",
                "Thirty minutes past eight","Thirty minutes past ten",
                "Thirty minutes past twelve","Thirty minutes past eight",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",12,12);

        WorkBook workBook89 = addLesson(db,89,"Esawan iongon atutubet",
                "esawan_iongon_atutubet"," ",null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                "Thirty minutes past ten","Esawan iongon atutubet",
                "sentence_construction",12,12);

        WorkBook workBook90 = addLesson(db,90,"Esawan ikany'aongon atutubet","thirty_minutes_past_three",
                " ",
                "Thirty minutes past five",
                "Thirty minutes past eight","Thirty minutes past three",
                "Thirty minutes past four","Thirty minutes past three",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",12,12);

        WorkBook workBook91 = addLesson(db,91,"Adakikan atomon akany alomar esawan iuni","quarter to nine",
                " ",
                "A quarter to nine",
                "A quarter to three","A quarter past nine",
                "A quarter to five","A quarter to nine",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",12,12);

        WorkBook workBook92 = addLesson(db,92,"Esawan ikany'ape atutubet",
                "esawan_ikanyape_atutubet"," ",null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                "Thirty minutes past twelve","Esawan ikany'ape atutubet",
                "sentence_construction",12,12);

        WorkBook workBook93 = addLesson(db,93,"Adakikan atomon akany atuboros esawa ediopet","quarter_past_seven",
                " ",
                "A quarter to seven",
                "A quarter past one","A quarter to one",
                "A quarter past seven","A quarter past seven",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",12,12);

        WorkBook workBook94 = addLesson(db,94,"Esawan ikany'aare","esawan_ikanyaare",
                " ",
                "One o'clock",
                "Five o'clock","Six o'clock",
                "Seven o'clock","One o'clock",null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                null,null,
                "word_quiz",12,12);

        WorkBook workBook95 = addLesson(db,95,"Esawan itomon adiop atutubet",
                "thirty_minutes_past_five"," ",null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,null,null,
                null,null,null,null,
                null,null,null,
                null,null,null,null,null,null,
                "Thirty minutes past five","Esawan itomon adiop atutubet",
                "sentence_construction",12,12);

        /**
         *
         * end of Clock - Time
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

        //Time
        //days
        Phrase phrase1 = addPhrase(db,1,"Lolo nabalasa","nabalasa","Today is Monday.",null,0,5,12);
        Phrase phrase2 = addPhrase(db,2,"Moyi nayareit","nayareit","Tomorrow is Tuesday.", null,0,5,12);
        Phrase phrase3 = addPhrase(db,3,"Iwalari naiuniet","naiuniet","The next day is Wednesday",null,0,5,12);
        Phrase phrase4 = addPhrase(db,4,"Ebogunete eduwe naiwongonet","naiwongonet","The children will come back on Thursday.",null,0,5,12);
        Phrase phrase5 = addPhrase(db,5,"Elosi ngesi naikanyet","naikanyet","She or He is going on Friday.",null,0,5,12);
        Phrase phrase6 = addPhrase(db,6,"Ejai esokoni namukaga","namukaga","The market day is on Saturday.",null,0,5,12);
        Phrase phrase7 = addPhrase(db,7,"Ilipete etunga nasabiiti","nasabiiti","People pray on Sunday.",null,0,5,12);

        //Time
        //months
        Phrase phrase8 = addPhrase(db,8,"Iraraite ikito akwi duc olap lo Orara","orara","Trees always shed off leaves in January", null,0,6,12);
        Phrase phrase9 = addPhrase(db,9,"Imukete itolumo duc olap lo Omuk","omuk","Trees always develop shades in February", null,0,6,12);
        Phrase phrase10 = addPhrase(db,10,"Ekwangitos itunga duc olap lo Okwang","okwang","People are always dirty preparing gardens in March",null,0,6,12);
        Phrase phrase11 = addPhrase(db,11,"Epol edou duc olap lo Odunge","odunge","The rains are always too heavy in April",null,0,6,12);
        Phrase phrase12 = addPhrase(db,12,"Ededeng etenge duc olap lo Opedelei","opedelei","There is always shortage of food in May",null,0,6,12);
        Phrase phrase13 = addPhrase(db,13,"Epol ebale duc olap lo Omaruk","omaruk","There are always mushrooms in June",null,0,6,12);
        Phrase phrase14 = addPhrase(db,14,"Ipu inyamat duc olap lo Omodokingol","omodokingol","There is always a lot of food in July",null,0,6,12);
        Phrase phrase15 = addPhrase(db,15,"Imokosi itunga duc olap lo Otikoi","otikoi","People are always satisfied in August",null,0,6,12);
        Phrase phrase16 = addPhrase(db,16,"Ebaritos itunga duc olap lo Otibar","otibar","A lot of riches always from harvest in September",null,0,6,12);
        Phrase phrase17 = addPhrase(db,17,"Ipu ipucito duc olap lo Osuban","osuban","Always a lot of functions in November",null,0,6,12);

        //Greetings
        Phrase phrase18 = addPhrase(db,18,"Ijai biai?","ijai_biai","How are you?",null,0,1,1);
        Phrase phrase19 = addPhrase(db,19,"Ikwenyunit biai","ikwenyunit_biai","Good morning",null,0,1,1);
        Phrase phrase20 = addPhrase(db,20,"Yoga","yoga","Hello or Hi","hello",0,1,1);
        Phrase phrase21 = addPhrase(db,21,"Ijaasi biai?","ijaasi_biai","How are you?(to many)",null,0,1,1);
        Phrase phrase22 = addPhrase(db,22,"Ejokuna, arai ijo?","ejokuna_arai_ijo","I am fine, how are you?",null,0,1,1);
        Phrase phrase23 = addPhrase(db,23,"Ejokuna, arai yesu?","ejokuna_arai_yesu","I am fine, how are you?(responding to many)",null,0,1,1);
        Phrase phrase24 = addPhrase(db,24,"Ekakiror erai...","ekakiror_erai","My name is...",null,0,1,1);
        Phrase phrase25 = addPhrase(db,25,"Ingai bo ekon kiror?","ingai_bo_ekon_kiror","What is your name?",null,0,1,1);
        Phrase phrase26 = addPhrase(db,26,"Eyalama airiamun","eyalama_ariamun","Pleased to meet you",null,0,1,1);
        Phrase phrase27 = addPhrase(db,27,"Eyalama awanyun","eyalama_awanyun","Pleased to see you",null,0,1,1);
        Phrase phrase28 = addPhrase(db,28,"Iboikin ber","iboikin_ber","Please sit",null,0,1,1);
        Phrase phrase29 = addPhrase(db,29,"Yoga do","yoga_do","Good bye",null,0,1,1);
        Phrase phrase30 = addPhrase(db,30,"Ojotor ojok","ojotor_ojok","Goodnight",null,0,1,1);

        //Expressing gratitude start
        Phrase phrase31 = addPhrase(db,31,"Eyalama","eyalama","Thank you",null,0,3,1);
        Phrase phrase32 = addPhrase(db,32,"Eyalamikini eong noi","eyalamikini_eong_noi","I will be so grateful",null,0,3,1);
        Phrase phrase33 = addPhrase(db,33, "Iyalama ngesi","iyalama_nyesi","She or He is thankful or grateful",null,0,3,1);
        Phrase phrase34 = addPhrase(db,34,"Iyalama ngesi noi","iyalama_nyesi_noi","He or She will be thankful or grateful",null,0,3,1);
        Phrase phrase35 = addPhrase(db,35,"Iyalamikin ngesi noi","iyalamikin_nyesi_noi","She or He will be so grateful or thankful",null,0,3,1);
        Phrase phrase36 = addPhrase(db,36,"Eong itelekaarit","eong_itelekaarit","The pleasure is mine",null,0,3,1);
        Phrase phrase37 = addPhrase(db,37,"Mam ilimuni","mam_ilimuni","Don’t mention it",null,0,3,1);
        Phrase phrase38 = addPhrase(db,38,"Mam ipodo","mam_ipodo","Never mind",null,0,3,1);
        Phrase phrase39 = addPhrase(db,39,"Mam ipodosi","mam_ipodosi","Never mind(to many)",null,0,3,1);

        //Travel
        Phrase phrase40 = addPhrase(db,40,"Akoto eong alosit Kampala","akoto_eong_alosit_kampala","I want to go to Kampala",null,0,7,13);
        Phrase phrase41 = addPhrase(db,41,"Aibo ijai jo?","aibo_ijai_jo","Where are you?",null,0,7,13);
        Phrase phrase42 = addPhrase(db,42,"Aibo ejai eisaawe lo idegei?","aibo_ejai_eisaawe_lo_idegei","Where is the airport?",null,0,7,13);
        Phrase phrase43 = addPhrase(db,43,"Okwe odumaki eong abaasi","okwe_odumaki_eong_abaasi","Please get me a bus",null,0,7,13);
        Phrase phrase44 = addPhrase(db,44,"Aibo ne?","aibo_ne","Where is this?",null,0,7,13);
        Phrase phrase45 = addPhrase(db,45,"Olot ngaren cut mam apar","olot_ngaren_cut_mam_apar","Walk straight ahead",null,0,7,13);
        Phrase phrase46 = addPhrase(db,46,"Okudo teten","okudo_teten","Turn right", "turn_right",0,7,13);
        Phrase phrase47 = addPhrase(db,47,"Kediany","kediany","Left",null,0,7,13);
        Phrase phrase48 = addPhrase(db,48,"Ingarakinai eong itodik ne dokunes","ingarakinai_eong_itodik_ne_dokunes","Help tell me where to get off",null,0,7,13);
        Phrase phrase49 = addPhrase(db,49,"Esawa bani enyouna abaasi?","esawa_bani_enyouna_abaasi","What time does the bus leave?",null,0,7,13);
        Phrase phrase50 = addPhrase(db,50,"Elosi ngesi Nyalakakimat.","elosi_nyesi_nyalakakimat","He or She is going South.",null,0,7,13);
        Phrase phrase51 = addPhrase(db,51,"Nyakoi alomutor eong","nyakoi_alomutor_eong","North is where I come from.", "north",0,7,13);
        Phrase phrase52 = addPhrase(db,52,"Ijesari akolong To.","ijesari_akolong_to","The sun sets in the West.",null,0,7,13);
        Phrase phrase53 = addPhrase(db,53,"Kide elomuna akolong","kide_elomuna_akolong","The sun rises from the East.",null,0,7,13);

        //Relations
        Phrase phrase54 = addPhrase(db,54,"Erai Akello acen ka.","erai_akello_acen_ka","Akello is my niece.",null,0,8,7);
        Phrase phrase55 = addPhrase(db,55,"Ocen ka ngon.","ocen_ka_ngon","That is my nephew",null,0,8,7);
        Phrase phrase56 = addPhrase(db,56,"Ebunit inacika.","ebunit_inac_ka","My sister is coming.",null,0,8,7);
        Phrase phrase57 = addPhrase(db,57,"Esiomi opajan ka","esiomi_opajan_ka","My cousin studies.",null,0,8,7);
        Phrase phrase58 = addPhrase(db,58,"Ingai ekiror apapa kon?","ingai_ekiror_apapa_kon","What is your fathers name?",null,0,8,7);
        Phrase phrase59 = addPhrase(db,59,"Ingai ekiror atoto kon?","ingai_ekiror_atoto_kon","What is your mother's name",null,0,8,7);
        Phrase phrase60 = addPhrase(db,60,"Papa ka ngesi je","papa_ka_ngesi_je","That is my grandfather",null,0,8,7);
        Phrase phrase61 = addPhrase(db,61,"Ejai tata ka musiri","ejai_tata_ka_musiri","My grandmother is in the garden",null,0,8,7);
        Phrase phrase62 = addPhrase(db,62,"Ija ka ngesi ngin","ija_ka_ngesi_ngin","That is my aunt",null,0,8,7);
        Phrase phrase63 = addPhrase(db,63,"Mamai ka ngesi Ochole","mamai_ka_ngesi_ochole","Ochole is my uncle",null,0,8,7);
        Phrase phrase64 = addPhrase(db,64,"Inac ka ngesi Akiror","inac_ka_ngesi_akiror", "Akiror is my sister",null,0,8,7);
        Phrase phrase65 = addPhrase(db,65,"Onac ka ngon","onac_ka_ngon","That is my brother",null,0,8,7);
        Phrase phrase66 = addPhrase(db,66,"Erasi ngun ika nacan","erasi_ngun_ika_nacan","Those are my siblings",null,0,8,7);
        Phrase phrase67 = addPhrase(db,67,"Anacan ngun","anacan_ngun","Those are sisters",null,0,8,7);
        Phrase phrase68 = addPhrase(db,68,"Imukeru bo angai ngin","imukeru_bo_angai_ngin","Whose baby is that?",null,0,8,7);
        Phrase phrase69 = addPhrase(db,69,"Ekoto ikoku alosit osomero","ekoto_ikoku_alosit_osomero","The child wants to go to school",null,0,8,7);
        Phrase phrase70 = addPhrase(db,70,"Iduwe ke Ikwap lu kere","iduwe_ke_ikwap_lu_kere","These are all Ikwap's children",null,0,8,7);
        Phrase phrase71 = addPhrase(db,71,"Apajan ka ngesi","apajan_ka_ngesi","She or he is my relative",null,0,8,7);
        Phrase phrase72 = addPhrase(db,72,"Ejai engo aberu ore","ejai_eong_aberu_ore","I have a wife at home",null,0,8,7);
        Phrase phrase73 = addPhrase(db,73,"Okilen ka ngon","okilen_ka_ngon","That is my husband",null,0,8,7);

        //Body parts
        Phrase phrase74 = addPhrase(db,74,"Ewojak ike tim","ewojak_ike_tim","Her or His hair is long",null,0,9,4);
        Phrase phrase75 = addPhrase(db,75,"Ekepitai eke reet","ekepitai_eke_reet","His or Her forehead was trimmed",null,0,9,4);
        Phrase phrase76 = addPhrase(db,76,"Epolok Joshua akonye","epolok_joshua_akonye","Joshua's eyes are big.","big_eyes",0,9,4);
        Phrase phrase77 = addPhrase(db,77,"Ewiyete apesse ikumes","ewiyete_apesse_ikumes","The girl's nose is bleeding",null,0,9,4);
        Phrase phrase78 = addPhrase(db,78,"Akituk inerare","akituk_inerare","The mouth is for talking.",null,0,9,4);
        Phrase phrase79 = addPhrase(db,79,"Aki epupere","aki_epupere","The ears are for listening",null,0,9,4);
        Phrase phrase80 = addPhrase(db,80,"Ebut ya amatenget","ebut_ya_amatenget","Her cheek is swollen",null,0,9,4);
        Phrase phrase81 = addPhrase(db,81,"Ipii ya emukule","ipii_ya_emukule","Her skin itches",null,0,9,4);
        Phrase phrase82 = addPhrase(db,82,"Akulu eburit adam.","akulu_eburit_adam","The skull covers the brain",null,0,9,4);
        Phrase phrase83 = addPhrase(db,83,"Ejai akou kuju omorosin","ejai_akou_kuju_omorosin","The head is on top of the neck",null,0,9,4);
        Phrase phrase84 = addPhrase(db,84,"Adam ngesi ewomomei","adam_ngesi_ewomomei", "The brain is the one that thinks",null,0,9,4);
        Phrase phrase85 = addPhrase(db,85,"Enonok angajep","enonok_angajep","The tongue is soft",null,0,9,4);
        Phrase phrase86 = addPhrase(db,86,"Eyii angirit ke","eyii_angirit_ke","His or Her gum is bleeding",null,0,9,4);
        Phrase phrase87 = addPhrase(db,87,"Ekwakang Ikela ikoku","ekwakang_ikela_ikoku","The baby's teeth are white.",null,0,9,4);
        Phrase phrase88 = addPhrase(db,88,"Irioko ya icop","irioko_ya_icop","Her pupil is black",null,0,9,4);
        Phrase phrase89 = addPhrase(db,89,"Eriebi je ekeper.","eriebi_je_ekeper","His shoulder is paining.",null,0,9,4);
        Phrase phrase90 = addPhrase(db,90,"Ejasi ikiliok kede amasurubun","ejasi_ikiliok_kede_amasurubun","Men have beards","beards",0,9,4);
        Phrase phrase91 = addPhrase(db,91,"Elayi eporoto abeeru angin","elayi_eporoto_abeeru_angin","That lady's voice is beautiful.","woman_sing",0,9,4);
        Phrase phrase92 = addPhrase(db,92,"Ewoja Okitoi egura","ewoja_okitoi_egura","Okitoi's spinal cord is long.",null,0,9,4);
        Phrase phrase93 = addPhrase(db,93,"Iwadik kede akan na oteten","iwadik_kede_akan_na_oteten","Write with the right hand.",null,0,9,4);
        Phrase phrase94 = addPhrase(db,94,"Epipil ake akan na okedyen","epipil_ake_akan_na_okedyen","His or her left hand is paining",null,0,9,4);
        Phrase phrase95 = addPhrase(db,95,"Ewuriaka ngesi ibookor","ewuriaka_ngesi_ibookor","His or Her fingers are short",null,0,9,4);
        Phrase phrase96 = addPhrase(db,96,"Ejai akirididi kwap akan","ejai_akirididi_kwap_akan", "Armpits are under the arm",null,0,9,4);
        Phrase phrase97 = addPhrase(db,97,"Ebilitai je amaraga","ebilitai_je_amaraga","His ribs were broken.",null,0,9,4);
        Phrase phrase98 = addPhrase(db,98,"Etakanete ngesi amori","etakanete_ngesi_amori","His or her veins are seen",null,0,9,4);
        Phrase phrase99 = addPhrase(db,99,"Ejai engo atorob na epol","ejai_engo_atorob_na_epol","I have a big chest.",null,0,9,4);
        Phrase phrase100 = addPhrase(db,100,"Erengak aokot","erengak_aokot","Blood is red",null,0,9,4);
        Phrase phrase101 = addPhrase(db,101,"Emunara eke emanyi","emunara_eke_manyi","His or her liver is spoilt",null,0,9,4);
        Phrase phrase102 = addPhrase(db,102,"Epol ingalur aswam","epol_ingalur_aswam","The kidneys have a lot of work.",null,0,9,4);
        Phrase phrase103 = addPhrase(db,103,"Elumorit ngesi aipul","elumorit_ngesi_aipul","His or Her navel is inside",null,0,9,4);
        Phrase phrase104 = addPhrase(db,104,"Epalal ngesi akanin","epalal_ngesi_akanin","His or Her palms are wet.",null,0,9,4);
        Phrase phrase105 = addPhrase(db,105,"Edit Akwi akoik","edit_akwi_akoik","Akwi's stomach is small",null,0,9,4);
        Phrase phrase106 = addPhrase(db,106,"Eriana ngesi epur","eriana_ngesi_epur","His or Her back is flat",null,0,9,4);
        Phrase phrase107 = addPhrase(db,107,"Iyenga oni kede iwukoi","iyenga_oni_kede_iwukoi","We breath through the lungs.",null,0,9,4);
        Phrase phrase108 = addPhrase(db,108,"Eriebi engo ececelu","eriebi_engo_ececelu","The groin is paining me.",null,0,9,4);
        Phrase phrase109 = addPhrase(db,109,"Epolok ya amuros","epolok_ya_amuros","Her thighs are big",null,0,9,4);
        Phrase phrase110 = addPhrase(db,110,"Ekukokina ya akungin ke","ekukokina_ya_akungin_ke","She knelt on her knees",null,0,9,4);
        Phrase phrase111 = addPhrase(db,111,"Egogong akojo","egogong_akojo","Bones are hard.",null,0,9,4);
        Phrase phrase112 = addPhrase(db,112,"Edisiak je akeje","edisiak_je_akeje","His legs are small",null,0,9,4);
        Phrase phrase113 = addPhrase(db,113,"Epipil engo ekurunyun","epipil_engo_ekurunyun","My kneecap is paining.",null,0,9,4);
        Phrase phrase114 = addPhrase(db,114,"Ejatator apesur isiepon","ejatator_apesur_isiepon","Girls have hips",null,0,9,4);
        Phrase phrase115 = addPhrase(db,115,"Elosete itunga kede akeje","elosete_itunga_kede_akeje","People walk on Foot",null,0,9,4);

        //clothing
        Phrase phrase116 = addPhrase(db,116, "Ecilil eiteteyi","ecilil_eiteteyi","The dress is torn.",null,0,1,5);
        Phrase phrase117 = addPhrase(db,117,"Erai ekoti ngon lo bululu","erai_ekoti_ngon_lo_bululu","That coat is blue.",null,0,10,5);
        Phrase phrase118 = addPhrase(db,118,"Itet epulani","itet_epulani","The vest is new.",null,0,1,5);
        Phrase phrase119 = addPhrase(db,119,"Ejai eke esati ocalo","ejai_eke_esati_ocalo","His shirt is in the village.",null,0,10,5);
        Phrase phrase120 = addPhrase(db,120,"Emojongit esiya","emojongit_esiya","The girl's beaded apron is old.",null,0,10,5);
        Phrase phrase121 = addPhrase(db,121, "Ebenen eimukes iduwe","ebenen_eimukes_iduwe", "The children's blanket is light.",null,0,10,5);
        Phrase phrase122 = addPhrase(db,122,"Epalal edabada","epalal_edabada","The trouser is wet.",null,0,10,5);
        Phrase phrase123 = addPhrase(db,123,"Elotari akopira","elotari_akopira","The hat has been washed.",null,0,10,5);
        Phrase phrase124 = addPhrase(db,124,"Edolit apese alega.","edolit_apese_alega","The bra fits the girl.",null,0,10,5);
        Phrase phrase125 = addPhrase(db,125,"Enapit esapat asokosin","enapit_esapat_asokosin","The boy is putting on stockings.",null,0,10,5);
        Phrase phrase126 = addPhrase(db,126,"Atayin adi bo ngun?","atayin_adi_bo_ngun","How many ties are those?",null,0,10,5);
        Phrase phrase127 = addPhrase(db,127,"Ipu eong atelei","ipu_eong_atelei","I have many aprons.",null,0,10,5);
        Phrase phrase128 = addPhrase(db,128,"Adolu ngesi kede abwos a Toto.","adolu_ngesi_kede_abwos_a_toto","She or He has arrived with mother's leather skirt",null,0,10,5);
        Phrase phrase129 = addPhrase(db,129,"Enapit ngesi egomasi.","enapit_ngesi_egomasi","She is putting on a gomasi",null,0,10,5);
        Phrase phrase130 = addPhrase(db,130,"Iriono ekanzu","iriono_ekanzu","The kanzu is dirty.",null,0,10,5);
        Phrase phrase131 = addPhrase(db,131,"Adonyokin atambala","adonyokin_atambala","The handkerchief has been sewed.",null,0,10,5);

        //Numbers
        Phrase phrase132 = addPhrase(db,132,"Abakasa adiopet","abakasa_ediopet","One envelope","one_envelope",0,2,1);
        Phrase phrase133 = addPhrase(db,133,"Abaasin aare","abaasin_aare","Two buses", "two_buses",0,2,1);
        Phrase phrase134 = addPhrase(db,134,"Abikirian auni","abikirian_auni","Three catholic nuns",null,0,2,1);
        Phrase phrase135 = addPhrase(db,135,"Abeei aongon" ,"abeei_aongon", "Four eggs", "foureggs",0,2,1);
        Phrase phrase136 = addPhrase(db,136,"Abiolosin akany'ape","abiolosin_akanyape","Six ambulances",null,0,2,1);
        Phrase phrase137 = addPhrase(db,137,"Abiroi akany'aare","abiroi_akanyaare","Seven big sticks(club)",null,0,2,1);
        Phrase phrase138 = addPhrase(db,138,"Abukui akanyauni","abukui_akanyauni","Eight book covers",null,0,2,1);
        Phrase phrase139 = addPhrase(db,139,"Abubuk akany'aongon","abubuk_akanyaongon","Nine groundnut shells or husks", "nine_groundnut_husks",0,2,1);
        Phrase phrase140 = addPhrase(db,140,"Abuwai atomon","abuwai_atomon","Ten caves",null,0,2,1);
        Phrase phrase141 = addPhrase(db,141,"Abootin atomon'adiop","abootin_atomonadiop","Eleven votes",null,0,2,1);
        Phrase phrase142 = addPhrase(db,142,"Aboolei atomon aare","aboolei_atomonaare","Twelve cobs",null,0,2,1);
        Phrase phrase143 = addPhrase(db,143,"Abongun atomon auni","abongun_atomon_auni","Thirteen bark clothes",null,0,2,1);
        Phrase phrase144 = addPhrase(db,144,"Abulesin atomon aongon","abulesin_atomon_aongon","Fourteen maid servants",null,0,2,1);
        Phrase phrase145 = addPhrase(db,145,"Aburaasin atomon akany","aburaasin_atomon_akany","Fifteen brushes",null,0,2,1);
        Phrase phrase146 = addPhrase(db,146,"Aburacun atomon akany'ape","aburacun_atomon_akanyape","Sixteen clasps",null,0,2,1);
        Phrase phrase147 = addPhrase(db,147,"Abureta atomon akany'aare","abureta_atomon_akanyaare","Seventeen wrappers or covers",null,0,2,1);
        Phrase phrase148 = addPhrase(db,148,"Abutuusin atomon akany'auni","abutuusin_atomon_akanyauni","Eighteen rubber or jungle boots.",null,0,2,1);
        Phrase phrase149 = addPhrase(db,149,"abuuban atomon akany'aongon","abuuban_atomon_akanyaongon","Nineteen tyre hand pumps",null,0,2,1);
        Phrase phrase150 = addPhrase(db,150,"Abwaadei akais-aare","abwaadei_akaisaare","Twenty prostitutes",null,0,2,1);
        Phrase phrase151 = addPhrase(db,151,"Abwacin akais-aare en diop","abwacin_akaisaare_endiop","Twenty one barren women",null,0,2,1);
        Phrase phrase152 = addPhrase(db,152,"Abwakan akais-aare alu aare","abwakan_akaisaare_alu_aare","Twenty two ear rings",null,0,2,1);
        Phrase phrase153 = addPhrase(db,153,"Ibaibulin akais-aare alu uni","ibaibulin_akaisaare_alu_uni","Twenty three bibles",null,0,2,1);
        Phrase phrase154 = addPhrase(db,154,"Ibaluan akais-aare alu wongon","ibaluan_akaisaare_alu_wongon","Twenty Four letters",null,0,2,1);
        Phrase phrase155 = addPhrase(db,155,"Ibajetin akais-aare alu kany","ibajetin_akaisaare_alu_kany","Twenty Five budgets",null,0,2,1);
        Phrase phrase156 = addPhrase(db,156,"Iboyan akais-aare alu kany'ape","iboyan_akaisaare_alu_kanyape","Twenty Six fish nets",null,0,2,1);
        Phrase phrase157 = addPhrase(db,157,"Icoloonin akais-aare alu kany'aare","icoloonin_akaisaare_alu_kanyaare","Twenty Seven pit latrines",null,0,2,1);
        Phrase phrase158 = addPhrase(db,158,"Ibirinyaanyan akais-aare alu kany'auni","ibirinyaanyan_akaisaare_alu_kanyauni","Twenty Eight egg plants",null,0,2,1);
        Phrase phrase159 = addPhrase(db,159,"Idiigai akais-aare alu kany'aongon","idiigai_akaisaare_alu_kanyaongon","Twenty Nine male sheep",null,0,2,1);
        Phrase phrase160 = addPhrase(db,160,"idoboi akais-auni","idoboi_akaisauni","Thirty big fishing hooks",null,0,2,1);
        Phrase phrase161 = addPhrase(db,161,"Iguudoi akais-auni en diop","iguudoi_akaisauni_en_diop","Thirty one roads",null,0,2,1);
        Phrase phrase162 = addPhrase(db,162,"Aditai akais-auni alu aare","aditai_akaisauni_alu_aare","Thirty two baskets",null,0,2,1);
        Phrase phrase163 = addPhrase(db,163,"Ayinakineta akais-auni alu uni","ayinakineta_akaisauni_alu_uni","Thirty three gifts",null,0,2,1);
        Phrase phrase164 = addPhrase(db,164,"Ibokor akais-auni alu wongon","ibokor_akaisauni_alu_wongon","Thirty four index fingers",null,0,2,1);
        Phrase phrase165 = addPhrase(db,165,"Eeburak akais-auni alu kany","eeburak_akaisauni_alu_kany","Thirty five debt collectors",null,0,2,1);
        Phrase phrase166 = addPhrase(db,166,"Idukurun akais-auni alu kany'ape","idukurun_akaisauni_alu_kanyape","Thirty six small huts",null,0,2,1);
        Phrase phrase167 = addPhrase(db,167,"Idonyisyo akais-auni alu kany'aare","idonyisyo_akaisauni_alu_kanyaare","Thirty seven sewing needles",null,0,2,1);
        Phrase phrase168 = addPhrase(db,168,"Igaalin akais-auni alu kany'auni","igaalin_akaisauni_alu_kanyauni","Thirty eight bicycles",null,0,2,1);
        Phrase phrase169 = addPhrase(db,169,"Ijeran akais-auni alu kany'aongon","ijeran_akaisauni_alu_kanyaongon","Thirty nine prisioners",null,0,2,1);
        Phrase phrase170 = addPhrase(db,170,"Eejaanakinak akais-aongon","eejaanakinak_akaisaongon","Forty servants or attendants",null,0,2,1);
        Phrase phrase171 = addPhrase(db,171,"Imaradadin akais-aongon en diop","imaradadin_akaisaongon_en_diop","Forty one decorations",null,0,2,1);
        Phrase phrase172 = addPhrase(db,172,"Iipoisyo akais-aongon alu aare","iipoisyo_akaisaongon_alu_aare","Forty two kitchens",null,0,2,1);
        Phrase phrase173 = addPhrase(db,173,"Ijiiso akais-aongon alu iuni","ijiiso_akaisaongon_alu_uni","Forty three weapons",null,0,2,1);
        Phrase phrase174 = addPhrase(db,174,"Ikadukok akais-aongon alu wongon","ikadukok_akaisaongon_alu_wongon","Fouty four builders",null,0,2,1);
        Phrase phrase175 = addPhrase(db,175,"Ikalumok akais-aongon alu kany","ikalumok_akaisaongon_alu_kany","Forty five swimmers",null,0,2,1);
        Phrase phrase176 = addPhrase(db,176,"Ikaru akais-aongon alu kany'ape","ikaru_akaisaongon_alu_kanyape","Forty six years",null,0,2,1);
        Phrase phrase177 = addPhrase(db,177,"Ikekia akais-aongon alu kany'aare","ikekia_akaisaongon_alu_kanyaare","Forty seven doors",null,0,2,1);
        Phrase phrase178 = addPhrase(db,178,"Ikebwokok akais-aongon alu kany'auni","ikebwokok_akaisaongon_alu_kanyauni","Forty eight porters",null,0,2,1);
        Phrase phrase179 = addPhrase(db,179,"Imieseko akais-aongon alu kany'aongon","imieseko_akaisaongon_alu_kanyaongon","Forty nine razor blades",null,0,2,1);
        Phrase phrase180 = addPhrase(db,180,"Ikapukok akais-akany","ikapukok_akaisakany","Fifty listeners or hearers",null,0,2,1);
        Phrase phrase181 = addPhrase(db,181,"Ikemerak akais-akany en diop","ikemerak_akaisakany_en_diop","Fifty one albinos",null,0,2,1);
        Phrase phrase182 = addPhrase(db,182,"Ikaarak akais-akany alu uni","ikaarak_akaisakany_alu_uni","Fifty three serial killers",null,0,2,1);
        Phrase phrase183 = addPhrase(db,183,"Ikiosikin akais-akany alu wongon","ikiosikin_akaisakany_alu_wongon","Fifty four kiosks",null,0,2,1);
        Phrase phrase184 = addPhrase(db,184,"Ikiliok akais-akany alu kany","ikiliok_akaisakany_alu_kany","Fifty five men",null,0,2,1);
        Phrase phrase185 = addPhrase(db,185,"Awojak akais-akany alu ikany'ape","awojak_akaisakany_alu_ikanyape","Fifty six tall people",null,0,2,1);
        Phrase phrase186 = addPhrase(db,186,"lukiyai akais-akany alu kany'aare","lukiyai_akaisakany_alu_kanyaare","Fifty seven first born boys",null,0,2,1);
        Phrase phrase187 = addPhrase(db,187,"Ikweei akais-akany alu kany'auni","ikweei_akaisakany_alu_kanyauni","Fifty eight foxes",null,0,2,1);
        Phrase phrase188 = addPhrase(db,188,"Amerak akais-akany alu kany'aongon","amerak_akaisakany_alu_kanyaongon","Fifty nine drunkards",null,0,2,1);
        Phrase phrase189 = addPhrase(db,189,"Imienai akais-akany'ape","imienai_akaisakanyape","Sixty bats",null,0,2,1);
        Phrase phrase190 = addPhrase(db,190,"Asujon akais-akany'ape en diop","asujon_akaisakanyape_en_diop","Sixty one pumpkins",null,0,2,1);
        Phrase phrase191 = addPhrase(db,191,"Amor akais-akany'ape alu aare","amor_akaisakanyape_alu_aare","Sixty two stones",null,0,2,1);
        Phrase phrase192 = addPhrase(db,192,"Imopiiran akais-akany'ape alu uni","imopiiran_akaisakanyape_alu_uni","Sixty three balls",null,0,2,1);
        Phrase phrase193 = addPhrase(db,193,"Imunwo akais-akany'ape alu wongon","imunwo_akaisakanyape_alu_wongon","Sixty four snakes",null,0,2,1);
        Phrase phrase194 = addPhrase(db,194,"Imusalaban akais-akany'ape alu kany","imusalaban_akaisakanyape_alu_kany","Sixty five crosses",null,0,2,1);
        Phrase phrase195 = addPhrase(db,195,"Inaabin akais-akany'ape alu ikany'aape","inaabin_akaisakanyape_alu_ikanyape","Sixty six prophets",null,0,2,1);
        Phrase phrase196 = addPhrase(db,196,"Imwalimun akais-akany'ape alu kany'aare","imwalimun_akaisakanyape_alu_kanyaare","Sixty seven teachers",null,0,2,1);
        Phrase phrase197 = addPhrase(db,197,"Imuudun akais-akany'ape ikany'auni","imuudun_akaisakanyape_ikanyauni","Sixty eight guns",null,0,2,1);
        Phrase phrase198 = addPhrase(db,198,"Ipalei akais-akanya'ape ikany'aongon","ipalei_akaisakanyape_ikanyaongon","Sixty nine trousers",null,0,2,1);
        Phrase phrase199 = addPhrase(db,199,"Ipapalin akais-akany'aare","ipapalin_akaisakanyaare","Seventy pawpaws",null,0,2,1);
        Phrase phrase200 = addPhrase(db,200,"Ipuliidan akais-akany'aare idiope","ipuliidan_akaisakanyaare_idiope","Seventy one lawyers",null,0,2,1);
        Phrase phrase201 = addPhrase(db,201,"Ipositan akais-akany'aare iare","ipositan_akaisakanyaare_iare","Seventy two post offices",null,0,2,1);
        Phrase phrase202 = addPhrase(db,202,"Ipiiriotin akais-akany'aare iuni","ipiiriotin_akaisakanyaare_iuni","Seventy three experts",null,0,2,1);
        Phrase phrase203 = addPhrase(db,203,"Ipeneesin akais-akany'aare iongon","ipeneesin_akaisakanyaare_iongon","Seventy four jack fruits",null,0,2,1);
        Phrase phrase204 = addPhrase(db,204,"Ipejok akais-akany'aare ikany","ipejok_akaisakanyaare_ikany","Seventy five male visitors",null,0,2,1);
        Phrase phrase205 = addPhrase(db,205,"Ipiin akais-akany'aare ikany'ape","ipiin_akaisakanyaare_ikanyape","Seventy six drinking tubes",null,0,2,1);
        Phrase phrase206 = addPhrase(db,206,"Iculin akais-akany'aare ikany'aare","iculin_akaisakanyaare_ikanyaare","Seventy seven wild cats",null,0,2,1);
        Phrase phrase207 = addPhrase(db,207,"Ingokwo akais-akany'aare ikany'auni","ingokwo_akaisakanyaare_ikanyauni","Seventy eight dogs",null,0,2,1);
        Phrase phrase208 = addPhrase(db,208,"Iduwe akais-akanyaare ikany'aongon","iduwe_akaisakanyaare_ikanyaongon","Seventy nine children",null,0,2,1);
        Phrase phrase209 = addPhrase(db,209,"Ikalotok akais-akany'auni","ikalotok_akaisakanyauni","Eighty travellers/passengers",null,0,2,1);
        Phrase phrase210 = addPhrase(db,210,"Lukibakor akais-akany'auni idiope","lukibakor_akaisakanyauni_idiope","Eighty one poor men",null,0,2,1);
        Phrase phrase211 = addPhrase(db,211,"Lukatir akais-akany'auni iare","lukatir_akaisakanyauni_iare","Eighty two giants",null,0,2,1);
        Phrase phrase212 = addPhrase(db,212,"Asigirian akais-akany'auni iuni","asigirian_akaisakanyauni_iuni","Eighty three female donkeys",null,0,2,1);
        Phrase phrase213 = addPhrase(db,213,"Arasasin akais-akany'auni iongon","arasasin_akaisakanyauni_iongon iuni","Eighty four bullets",null,0,2,1);
        Phrase phrase214 = addPhrase(db,214,"Aswa akais-akany'auni ikany","aswa_akaisakanyauni_ikany","Eighty five iron bars",null,0,2,1);
        Phrase phrase215 = addPhrase(db,215,"Atak akais-akany'auni ikany'ape","atak_akaisakanyauni_ikanyape","Eighty six female calves",null,0,2,1);
        Phrase phrase216 = addPhrase(db,216,"Atakerin akais-akany'auni ikany'aare","atakerin_akaisakanyauni_ikanyaare","Eighty seven boats",null,0,2,1);
        Phrase phrase217 = addPhrase(db,217,"Ataayin akais-akany'auni ikany'auni","ataayin_akaisakanyauni_ikanyaare","Eighty eight neck ties",null,0,2,1);
        Phrase phrase218 = addPhrase(db,218,"Atapengo akais-akany'auni ikany'aongon","atapengo_akaisakanyauni_ikanyaongon","Eighty nine guinea fowl",null,0,2,1);
        Phrase phrase219 = addPhrase(db,219,"Atiboloka akais-akany'aongon","atiboloka_akaisakanyaongon","Ninety pieces of broken pot",null,0,2,1);
        Phrase phrase220 = addPhrase(db,220,"Ataagoro akais-akany'aongon idiope","ataagoro_akaisakanyaongon_idiope","Ninety one roots",null,0,2,1);
        Phrase phrase221 = addPhrase(db,221,"Atanin akais-akany'aongon iare","atanin_akaisakanyaongon_iare","Ninety two wells",null,0,2,1);
        Phrase phrase222 = addPhrase(db,222,"Ataparin akais-akany'aongon iuni","ataparin_akaisakanyaongon_iuni","Ninety three ponds",null,0,2,1);
        Phrase phrase223 = addPhrase(db,223,"Ibaalen akais-akany'aongon iongon","ibaalen_akaisakanyaongon_iongon","Ninety four mushrooms",null,0,2,1);
        Phrase phrase224 = addPhrase(db,224,"Iboomun akais-akany'aongon ikany","iboomun_akaisakanyaongon_ikany","Ninety five bombs",null,0,2,1);
        Phrase phrase225 = addPhrase(db,225,"Icokai akais-akany'aongon ikany'ape","icokai_akaisakanyaongon_ikanyape","Ninety six pieces of chalk",null,0,2,1);
        Phrase phrase226 = addPhrase(db,226,"Aderekai akais-akany'aongon ikany'aare","aderekai_akaisakanyaongon_ikanyaare","Ninety seven calabashes",null,0,2,1);
        Phrase phrase227 = addPhrase(db,227,"Iderepan akais-akany'aongon ikany'auni","iderepan_akaisakanyaongon_ikanyauni","Ninety eight drivers",null,0,2,1);
        Phrase phrase228 = addPhrase(db,228,"Adakiikan akais-akany'aongon ikany'aongon","adakiikan_akaisakanyaongon_ikanyaongon","Ninety nine minutes",null,0,2,1);
        Phrase phrase229 = addPhrase(db,229,"Egworok akwatat","egworok_akwatat","One hundred mourners",null,0,2,1);

        //Weather
        Phrase phrase230 = addPhrase(db,230,"Elai akuj","elai_akuj","The sky is beautiful","sky",0,11,13);
        Phrase phrase231 = addPhrase(db,231,"Ededeng alilim","ededeng_alilim","The weather is cold",null,0,11,13);
        Phrase phrase232 = addPhrase(db,232,"Ekuwa edou","ekuwa_adou","The rain has cleared",null,0,11,13);
        Phrase phrase233 = addPhrase(db,233,"Adoo akako","adoo_akako","It rained hailstones",null,0,11,13);
        Phrase phrase234 = addPhrase(db,234,"Adolu akaale","adolu_akaale","The floods have come",null,0,11,13);
        Phrase phrase235 = addPhrase(db,235,"Ededeng eres","ededeng_eres","The thunder is too much",null,0,11,13);
        Phrase phrase236 = addPhrase(db,236,"Etepi edou","etepi_edou","The rain is falling","raining",0,11,13);
        Phrase phrase237 = addPhrase(db,237,"Ekusi ekuwam","ekusi_ekuwam","The wind is blowing",null,0,11,13);
        Phrase phrase238 = addPhrase(db,238,"Imilia epiyai","imilia_epiyai","It's lightening","lightening",0,11,13);
        Phrase phrase239 = addPhrase(db,239,"Adiloki ekuna","adiloki_ekuna","The mist has started early",null,0,11,13);
        Phrase phrase240 = addPhrase(db,240,"Irikina etaluka","irikina_etaluka","The rainbow is across","rainbow",0,11,13);
        Phrase phrase241 = addPhrase(db,241,"Eroko anya edakitos akop","eroko_anya_edakitos_akop","The grass is still carrying dew",null,0,11,13);
        Phrase phrase242 = addPhrase(db,242,"Ayapu aporo","ayapu_aporo","Wet season is soon",null,0,11,13);
        Phrase phrase243 = addPhrase(db,243,"Acaou akolong","acaou_akolong","The sun is shining",null,0,11,13);
        Phrase phrase244 = addPhrase(db,244,"Ededeng amwanis","ededeng_amwanis","The heat is too much",null,0,11,13);
        Phrase phrase245 = addPhrase(db,245,"Ayapu akamu","ayapu_akamu","Dry season is soon",null,0,11,13);

        //Clock - Time
        Phrase phrase246 = addPhrase(db,246,"Esawa ediopet","esawa_ediopet","One hour",null,0,12,12);
        Phrase phrase247 = addPhrase(db,247,"Adakikan akany","adakikan_akany","Five minutes",null,0,12,12);
        Phrase phrase248 = addPhrase(db,248,"Atikitikin atomon","atikitikin_atomon","Ten seconds",null,0,12,12);
        Phrase phrase249 = addPhrase(db,249,"Esawa ediopet","esawa_ediopet","Seven o'clock",null,0,12,12);
        Phrase phrase250 = addPhrase(db,250,"Esawan iare","esawan_iare","Eight o'clock",null,0,12,12);
        Phrase phrase251 = addPhrase(db,251,"Esawan iuni","esawan_iuni","Nine o'clock",null,0,12,12);
        Phrase phrase252 = addPhrase(db,252,"Esawan iongon","esawan_iongon","Ten o'clock",null,0,12,12);
        Phrase phrase253 = addPhrase(db,253,"Esawan ikany","esawan_ikany","Eleven o'clock",null,0,12,12);
        Phrase phrase254 = addPhrase(db,254,"Esawan ikany'ape","esawan_ikanykape","Twelve o'clock",null,0,12,12);
        Phrase phrase255 = addPhrase(db,255,"Esawan ikany'aare","esawan_ikanyaare","One o'clock",null,0,12,12);
        Phrase phrase256 = addPhrase(db,256,"Esawan ikany'auni","esawan_ikanyauni","Two o'clock",null,0,12,12);
        Phrase phrase257 = addPhrase(db,257,"Esawan ikany'aongon","esawan_ikanyaongon","Three o'clock",null,0,12,12);
        Phrase phrase258 = addPhrase(db,258,"Esawan itomon","esawan_itomon","Four o'clock",null,0,12,12);
        Phrase phrase259 = addPhrase(db,259,"Esawan itomon'adiop","esawan_itomonadiop","Five o'clock",null,0,12,12);
        Phrase phrase260 = addPhrase(db,260,"Esawan itomon aare","esawan_itomon_aare","Twelve o'clock",null,0,12,12);
        Phrase phrase261 = addPhrase(db,261,"Esawa ediopet atutubet","esawa_ediopet_atutubet","Thirty minutes past seven",null,0,12,12);
        Phrase phrase262 = addPhrase(db,262,"Esawan iare atutubet","esawan_iare_atutubet","Thirty minutes past eight",null,0,12,12);
        Phrase phrase263 = addPhrase(db,263,"Esawan iuni atutubet","esawan_iuni_atutubet","Thirty minutes past nine",null,0,12,12);
        Phrase phrase264 = addPhrase(db,264,"Esawan iongon atutubet","esawan_iongon_atutubet","Thirty minutes past ten",null,0,12,12);
        Phrase phrase265 = addPhrase(db,265,"Esawan ikany atutubet","esawan_ikany_atutubet","Thirty minutes past eleven",null,0,12,12);
        Phrase phrase266 = addPhrase(db,266,"Esawan ikany'ape atutubet","esawan_ikanyape_atutubet","Thirty minutes past twelve",null,0,12,12);
        Phrase phrase267 = addPhrase(db,267,"Esawan ikany'aare atutubet","thirty_minutes_past_one","Thirty minutes past one",null,0,12,12);
        Phrase phrase268 = addPhrase(db,268,"Esawan ikany'auni atutubet","thirty_minutes_past_two","Thirty minutes past two",null,0,12,12);
        Phrase phrase269 = addPhrase(db,269,"Esawan ikany'aongon atutubet","thirty_minutes_past_three","Thirty minutes past three",null,0,12,12);
        Phrase phrase270 = addPhrase(db,270,"Esawan itomon atutubet","thirty_minutes_past_four","Thirty minutes past four",null,0,12,12);
        Phrase phrase271 = addPhrase(db,271,"Esawan itomon adiop atutubet","thirty_minutes_past_five","Thirty minutes past five",null,0,12,12);
        Phrase phrase272 = addPhrase(db,272,"Esawan itomon aare atutubet","thirty_minutes_past_six","Thirty minutes past six",null,0,12,12);
        Phrase phrase273 = addPhrase(db,273,"Adakikan atomon akany alomar esawa ediopet","quarter_to_seven","A quarter to seven",null,0,12,12);
        Phrase phrase274 = addPhrase(db,274,"Adakikan atomon akany alomar esawan iare","quarter_to_eight","A quarter to eight",null,0,12,12);
        Phrase phrase275 = addPhrase(db,275,"Adakikan atomon akany alomar esawan iuni","quarter to nine","A quarter to nine",null,0,12,12);
        Phrase phrase276 = addPhrase(db,276,"Adakikan atomon akany alomar esawan iongon","quarter_to_ten","A quarter to ten",null,0,12,12);
        Phrase phrase277 = addPhrase(db,277,"Adakikan atomon akany alomar esawan ikany","quarter_to_eleven","A quarter to eleven",null,0,12,12);
        Phrase phrase278 = addPhrase(db,278,"Adakikan atomon akany alomar esawan ikany'ape","quarter_to_twelve","A quarter to twelve",null,0,12,12);
        Phrase phrase279 = addPhrase(db,279,"Adakikan atomon akany alomar esawan ikany'aare","quarter_to_one","A quarter to one",null,0,12,12);
        Phrase phrase280 = addPhrase(db,280,"Adakikan atomon akany alomar esawan ikany'auni","quarter_to_two","A quarter to two",null,0,12,12);
        Phrase phrase281 = addPhrase(db,281,"Adakikan atomon akany alomar esawan ikany'aongon","quarter_to_three","A quarter to three",null,0,12,12);
        Phrase phrase282 = addPhrase(db,282,"Adakikan atomon akany alomar esawan itomon","quarter_to_four","A quarter to four",null,0,12,12);
        Phrase phrase283 = addPhrase(db,283,"Adakikan atomon akany alomar esawan itomon adiop","quarter_to_five","A quarter to five",null,0,12,12);
        Phrase phrase284 = addPhrase(db,284,"Adakikan atomon akany alomar esawan itomon aare","quarter_to_six","A quarter to six",null,0,12,12);
        Phrase phrase285 = addPhrase(db,285,"Adakikan atomon akany atuboros esawa ediopet","quarter_past_seven","A quarter past seven",null,0,12,12);
        Phrase phrase286 = addPhrase(db,286,"Adakikan atomon akany atuboros esawan iyare","quarter_past_eight","A quarter past eight",null,0,12,12);
        //Phrase phrase287 = addPhrase(db,287,"Adakikan atomon akany atuboros esawan iuni","quarter_past_nine","A quarter past nine",0,12,12);
        //Phrase phrase288 = addPhrase(db,288,"Adakikan atomon akany alomar esawan iongon","quarter_to_ten","A quarter to ten",0,12,12);
        //Phrase phrase289 = addPhrase(db,289,"Adakikan atomon akany alomar esawan iongon","quarter_to_ten","A quarter to ten",0,12,12);


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
