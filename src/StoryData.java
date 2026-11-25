/**
 * All story text lives here so you can edit the story without touching UI code.
 * Requires Java 15+ (text blocks). If you're on an older version, tell me and I'll convert it.
 */
public class StoryData {

    // Index 0 = Part 1 ... index 3 = Part 4 (Part 5 is handled by EndingDialog)
    public static final String[] TITLES = {
        "Part 1: The Seamstress",
        "Part 2: The Young Traveler",
        "Part 3: The Singer",
        "Part 4: The Writer"
    };

    public static final String[] BODY = {
        // PART 1
        """
        In a village surrounded by rice fields lived an old woman everyone knew: Lola Isyang. She sewed clothes for the entire village. There was no wedding, baptism, or funeral where people didn't wear something made by her hands. Whenever she finished a piece, she counted the buttons, and each one came with a short prayer.

        "A barong only needs twenty buttons," she told her grandchild, "but every button carries a hidden prayer for whoever wears it."

        One night of heavy rain, the river overflowed and swept away the whole village, including Lola Isyang's house and the last garment she was sewing. When the flood passed, nothing was left. Little by little, even her name stopped being spoken by the survivors, until no one remembered her at all.

        But one button was not swept away. It was stuck to a page of the book, and it was identical to the button on the reader's own clothes.
        """,

        // PART 2
        """
        Tonyo was eight years old when he began drawing maps. Not for treasure, but so that anyone who came after him would never get lost.

        "Ate, look!" he said, holding up a paper crowded with drawings. "Here's the river, here's the mountain, and here's our house. That way, no matter where I end up, I'll know I have somewhere to come back to."

        He grew up with dreams bigger than the village. One morning he packed a bag, a pencil, and a few sheets of paper, and said goodbye. He was going to find the edge of the world, and he promised to return with a map of everywhere he'd been.

        He traveled farther and farther. The longer he walked, the wider his map grew, but the path home faded. He never came back, and the map was never finished.

        In the middle of the sea on his last drawing, there was a tiny mark shaped like a book. Tonyo never knew why he drew it. It felt as if something had pulled his hand.
        """,

        // PART 3
        """
        Lorena was once known across the whole province. When she sang in the plaza, everyone stopped, even the market vendors and the children at play. They said her voice could lighten the heaviest of days.

        She wrote one song for her only child, on the night the child was born. The last line held a single name.

        But one year, Lorena's voice began to fade. First she couldn't reach the high notes. Then she could only whisper. Until one morning, not a single sound came out of her mouth.

        "I wasn't afraid of losing my voice," she wrote in a letter she left behind. "I was afraid that no one would listen once I was silent."

        Because she no longer sang, no one went looking for her. Her name slipped out of the town's memory. Even the song she wrote for her child was left without an owner, and the name in its last line slowly faded away.

        That same fading can be seen on the earliest pages of the book.
        """,

        // PART 4
        """
        Old Man Basyo made the book. In his youth, he saw many people suffering over the loved ones they had lost, and he pitied them, so he found a way to help.

        "I thought I was being merciful," he said. "I believed that if you forget the pain, it goes away."

        He wrote the book with ancient ink. When someone wished to forget, their loved one's name was written on a page, and the pain left the heart of the one who stayed behind. In the first years, it seemed like a miracle.

        But forgetting doesn't know how to choose. Along with the pain, the smiles, the laughter, and the hugs were erased too. The people who forgot were left with a hollow inside that they couldn't explain.

        By the time Old Man Basyo understood his mistake, it was too late. He could not destroy the book, so he gave it a Keeper: a living person outside the book who holds the pen. The Keeper writes each new page whenever someone is forgotten.

        "She is alive," Old Man Basyo told the reader, looking at the pen in their hand. "She's out there, and she doesn't know she's the one holding the pen."

        It was the same pen the reader had been holding since the very beginning.
        """
    };

    // Short line shown at the bottom of each pop-up (the "hook")
    public static final String[] QUOTES = {
        "The people inside the book are real. They are not make-believe. They were only forgotten. And what is forgotten can be remembered.",
        "Everyone forgotten leaves a trace in the world: a map, a song, a piece of clothing. As long as there is a trace, there is hope.",
        "The book is not evil. It protects the forgotten so they won't disappear completely. But someone writes each of its pages. Who could it be?",
        "The Keeper is not a character in this story. She is a real person, and you are about to meet her."
    };

    // ---------- PART 5 ----------
    public static final String PART5_TITLE = "Part 5: The Last Page";

    public static final String PART5_REVEAL =
        """
        The last page is blank. But slowly, letters begin to appear one by one, as if someone is writing them from inside the air.

        Liwayway.

        She is the reader. She is the Keeper.

        Everything came back. Lola Isyang, who taught her to sew buttons when she was little. Tonyo, the younger brother who always trailed after her with a pencil in his hand. Lorena, her mother, who sang to her every night before bed.

        One by one, they were lost. Lola, to the flood. Tonyo, to a journey he never returned from. Mother, to an illness that took her voice and eventually her life. Within a few years, Liwayway lost nearly everyone she loved.

        She couldn't bear it. One night, she found the book Old Man Basyo had left behind and picked up the pen. She wrote down each of their names. And finally, she wrote her own.

        "I thought if I forgot them, it wouldn't hurt anymore."

        But she was still here, inside the book, searching for them. She learned that pain doesn't disappear when you forget. It only becomes a hole.

        Now she holds the pen, and she has to choose.
        """;

    public static final String CHOICE_PROMPT = "What will Liwayway choose?";
    public static final String CHOICE_A = "A. Stay inside the book";
    public static final String CHOICE_B = "B. Write their names into the world";

    public static final String ENDING_A =
        """
        Liwayway holds the pen over the page. "I'll stay here," she whispers. "Here, I'm with all of them."

        Lola Isyang's voice, soft and warm: "My child, you don't have to hide in order to be loved by us."

        But Liwayway has already made her choice. The book closes, and no one will ever open it again.

        THE END
        """;

    public static final String ENDING_B =
        """
        Liwayway presses the pen to the page and begins to write.

        "Lola Isyang. Tonyo. Mother Lorena."

        She writes each name, and the pages begin to glow.

        "I won't pretend anymore that they were never part of my life. I'll carry them with me, even if it hurts."

        The book opens, and a world is waiting.

        THE END
        """;
}
