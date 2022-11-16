import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.text.AttributeSet;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GetADB extends ListenerAdapter
{

	static GetADB MyInstance;

	final String DefaultText = "DiscordBotTokenをここに入れてください";
	final Font DefaultFont = new Font("�l�r �S�V�b�N", Font.PLAIN, 24);
	JFrame ExcuteWindow;
	JPanel ExcutePanel;
	JTextField Token;
	JButton ExcuteButton;
	JTextPane OutputTextPane;
	String OutputText = "";
	DefaultStyledDocument DSD = new DefaultStyledDocument(new StyleContext());
	SimpleAttributeSet attribute = new SimpleAttributeSet();
	JScrollPane OutputTextScroll;

	JDA jda;

	public static void main(String[] args)
	{
		MyInstance = new GetADB();
		MyInstance.createWindow();
	}

	private void createWindow()
	{
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			ExcuteWindow = new JFrame();
			ExcuteWindow.setBounds(0,
				0,
				500,
				250);
			ExcuteWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			createComponent();
			ExcuteWindow.setVisible(true);
			ExcuteButton.requestFocusInWindow();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createComponent()
	{
		Token = new JTextField(DefaultText);
		Token.setBounds(0,
			0,
			100,
			100);
		Token.setFont(DefaultFont);
		Token.setForeground(Color.gray);
		Token.setColumns(20);
		Token.addCaretListener(e ->
		{
			if (!Token.getText().equals(DefaultText))
				return;
			Token.setForeground(Color.black);
			Token.setText("");
		});
		Token.transferFocus();
		Token.setBackground(Color.white);

		ExcuteButton = new JButton();
		ExcuteButton.setText("Get Badge");
		ExcuteButton.setPreferredSize(new Dimension(100, 50));
		ExcuteButton.addActionListener(e ->
		{
			changeOutput("Connecting",
				TextColor(Color.black));
			buildJDA();
		});

		OutputTextPane = new JTextPane(DSD);
		OutputTextPane.setEditable(false);
		OutputTextPane.setPreferredSize(new Dimension(480, 100));

		OutputTextScroll = new JScrollPane();
		OutputTextScroll.setViewportView(OutputTextPane);

		ExcutePanel = new JPanel();
		ExcutePanel.add(Token);
		ExcutePanel.add(ExcuteButton);
		ExcutePanel.add(OutputTextScroll);

		ExcuteWindow.add(ExcutePanel,
			BorderLayout.CENTER);
		ExcuteWindow.setTitle("Discord Active Developer Badge");
	}

	private void buildJDA()
	{
		try {
			jda = JDABuilder.createDefault(Token.getText().replaceAll("[^A-Za-z0-9\\.\\-\\_]",
				"")).addEventListeners(this).setActivity(Activity.playing("Discord")).build();
			jda.awaitReady();
			changeOutput("Connected",
				TextColor(Color.black));
			changeOutput("このURLを使用してBotを自分のサーバーに招待してください:" + jda.getInviteUrl(),
				TextColor(Color.blue));
			jda.upsertCommand("get",
				"このコマンドを使用してください").queue();
		} catch (Exception e) {
			if (e instanceof InvalidTokenException) {
				changeOutput("無効なToken",
					TextColor(Color.red));
			}
			//e.printStackTrace();
		}
	}

	private AttributeSet TextColor(Color color)
	{
		attribute.addAttribute(StyleConstants.Foreground,
			color);
		return attribute;
	}

	private void changeOutput(String text, AttributeSet attribute)
	{
		try {
			DSD.insertString(DSD.getLength(),
				">>" + text + "\n",
				attribute);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onGuildJoin(GuildJoinEvent event)
	{
		changeOutput("サーバーに参加:" + event.getGuild().getName(),
			TextColor(Color.black));
		if (event.getGuild().getCommunityUpdatesChannel() == null) {
			changeOutput("このサーバーはコミュニティ機能が無効です。有効にしてください。",
				TextColor(Color.black));
			return;
		}
		changeOutput("/get コマンドを使用してください コマンドが表示されるまで最大1時間ほどかかる場合があります。",
			TextColor(Color.black));
	}

	@Override
	public void onSlashCommandInteraction(SlashCommandInteractionEvent event)
	{
		if (event.getGuild().getCommunityUpdatesChannel() == null) {
			changeOutput("このサーバーはコミュニティ機能が無効です。有効にしてください。",
				TextColor(Color.black));
			return;
		}
		if (event.getName().equals("get")) {
			event.deferReply().queue();
			event.getHook().sendMessage(
				"コマンドの実行が完了しました。\nhttps://discord.com/developers/active-developer よりバッジを申請してください。\n申請が可能になるのに最大で24時間かかる場合があります。\nまたBotTokenはリセットされるようお願いいたします。").queue();
			changeOutput(
				"コマンドの実行が完了しました。\nhttps://discord.com/developers/active-developer よりバッジを申請してください。\n申請が可能になるのに最大で24時間かかる場合があります。\nまたBotTokenはリセットされるようお願いいたします。",
				TextColor(Color.black));
		}
	}
}
