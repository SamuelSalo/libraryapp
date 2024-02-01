package com.samuelsalo.libraryapp.views.library;

import com.samuelsalo.libraryapp.entity.Book;
import com.samuelsalo.libraryapp.service.BookService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.selection.SelectionEvent;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.Optional;

@PageTitle("Library")
@Route(value = "library")
@RouteAlias(value = "")
public class LibraryView extends VerticalLayout {

    private final BookService bookService;
    private final UI ui;
    private Long selectedId;
    private TextField titleField = new TextField("Title");
    private TextField authorField = new TextField("Author");
    private TextArea descriptionArea = new TextArea("Description");
    private Grid<Book> bookGrid = new Grid<Book>(Book.class, false);
    private Button saveNewButton = new Button("Save New", new Icon(VaadinIcon.CHECK));
    private Button saveButton = new Button("Save", new Icon(VaadinIcon.WRENCH));
    private Button deleteButton = new Button("Delete", new Icon(VaadinIcon.TRASH));

    //Page layout constructor
    public LibraryView(BookService _bookService) {
        //Get UI and book rest service
        ui = UI.getCurrent();
        bookService = _bookService;

        setPadding(false);
        VerticalLayout page = new VerticalLayout();

        //Create navbar, icon, header
        Icon icon = new Icon(VaadinIcon.BOOK);
        icon.getStyle().setColor("white");
        H1 headerLogo = new H1("Library App");
        headerLogo.getStyle().setColor("white");
        headerLogo.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.MEDIUM);
        HorizontalLayout header = new HorizontalLayout(icon, headerLogo);
        header.addClassName("headerLayout");
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.addClassNames(LumoUtility.Padding.Vertical.NONE, LumoUtility.Padding.Horizontal.MEDIUM);
        add(header, page);

        //Create book grid
        bookGrid.addColumn(Book::getTitle).setHeader("Title");
        bookGrid.addColumn(Book::getAuthor).setHeader("Author");
        bookGrid.addColumn(Book::getDescription).setHeader("Description");
        bookGrid.setItems(bookService.getBooks().doOnError(err ->{
            Notification notification = Notification.show("Creating Book Failed: " + err.getMessage());
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }).block());
        bookGrid.addSelectionListener(selectionEvent -> GridSelection(selectionEvent));
        bookGrid.setSizeUndefined();

        //Init buttons & text fields
        titleField.setWidthFull();
        authorField.setWidthFull();
        descriptionArea.setWidthFull();

        saveNewButton.addClickListener(buttonClickEvent -> SaveNewButtonClicked());
        saveButton.addClickListener(buttonClickEvent -> UpdateButtonClicked());
        deleteButton.addClickListener(buttonClickEvent -> DeleteButtonClicked());

        deleteButton.setEnabled(false);
        saveButton.setEnabled(false);

        //Create site layout
        HorizontalLayout buttonsLayout = new HorizontalLayout(saveNewButton, saveButton, deleteButton);
        VerticalLayout leftSideLayout = new VerticalLayout(titleField, authorField, descriptionArea, buttonsLayout);
        leftSideLayout.setSizeUndefined();
        HorizontalLayout siteLayout = new HorizontalLayout(leftSideLayout, bookGrid);
        siteLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        siteLayout.setWidthFull();
        page.add(siteLayout);
    }


    /**
     * @param event
     * Grid selection change event.
     * Change buttons enabled states, fill fields, store Id of selected book, etc
     * based on selection empty or not
     */
    public void GridSelection(SelectionEvent<Grid<Book>, Book> event)
    {
        if(event.getAllSelectedItems().isEmpty())
        {
            saveNewButton.setEnabled(true);
            deleteButton.setEnabled(false);
            saveButton.setEnabled(false);
            selectedId = null;
        }
        else
        {
            saveNewButton.setEnabled(false);
            saveButton.setEnabled(true);
            deleteButton.setEnabled(true);

            Optional<Book> bookOptional = event.getFirstSelectedItem();
            if (bookOptional.isPresent())
            {
                titleField.setValue(bookOptional.get().getTitle());
                authorField.setValue(bookOptional.get().getAuthor());
                descriptionArea.setValue(bookOptional.get().getDescription());
                selectedId = bookOptional.get().getId();
            }
        }
    }

    /**
     * "Save New" button click event, send new book request, udpate UI accordingly
     */
    public void SaveNewButtonClicked()
    {
        if((!titleField.isEmpty()) && (!authorField.isEmpty()) && (!descriptionArea.isEmpty()))
        {
            bookService.createBook(new Book(titleField.getValue(), authorField.getValue(), descriptionArea.getValue()))
                    .doOnSuccess(succ->{
                        ui.access(() -> {
                            bookGrid.setItems(bookService.getBooks().doOnError(err ->{
                                        Notification notification = Notification.show("Creating Book Failed: " + err.getMessage());
                                        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                                    }).block());
                            Notification notification = Notification.show("New Book Added Successfully!");
                            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                        });

                    }).doOnError(err ->{
                        ui.access(() -> {
                            Notification notification = Notification.show("Creating Book Failed: " + err.getMessage());
                            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                        });
                    }).subscribe();

            titleField.setValue("");
            authorField.setValue("");
            descriptionArea.setValue("");
        }
    }

    /**
     * "Delete" button click event, send delete book request and update UI
     */
    public void DeleteButtonClicked()
    {
        bookService.deleteBook(selectedId)
            .doOnSuccess(succ->{
                ui.access(() -> {
                    bookGrid.setItems(bookService.getBooks().doOnError(err ->{
                        Notification notification = Notification.show("Creating Book Failed: " + err.getMessage());
                        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    }).block());

                    Notification notification = Notification.show("Book Deleted Successfully!");
                    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                });
            }).doOnError(err ->{
                    ui.access(() -> {
                        Notification notification = Notification.show("Deleting Book Failed: " + err.getMessage());
                        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    });
            }).subscribe();

        titleField.setValue("");
        authorField.setValue("");
        descriptionArea.setValue("");
    }

    /**
     * "Save" button click event, send put request and update ui accordingly
     */
    public void UpdateButtonClicked()
    {
        bookService.updateBook(new Book(titleField.getValue(), authorField.getValue(), descriptionArea.getValue()), selectedId)
            .doOnSuccess(succ->{
                ui.access(() -> {
                    bookGrid.setItems(bookService.getBooks().doOnError(err ->{
                        Notification notification = Notification.show("Creating Book Failed: " + err.getMessage());
                        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    }).block());
                    Notification notification = Notification.show("Book Updated Successfully!");
                    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                });
            }).doOnError(err ->{
                ui.access(() -> {
                    Notification notification = Notification.show("Updating Book Failed: " + err.getMessage());
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                });
            }).subscribe();
    }
}