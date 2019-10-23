package org.ali;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.validator.IntegerRangeValidator;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.textfield.TextField;


import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Route("")
public class MainView extends VerticalLayout {
    TextField namerField, quantifierField;
    ComboBox<String> breadTypeSelector, breadSelector;
    Button ordererButton;
    Div perpendicularErrorer;
    Binder<BreadOrderer> binder;
    List<BreadOrderer> breadOrderers;
    BreadOrderer savedOrder;





    private Grid<BreadOrderer> breadOrdererGridder = new Grid<>(BreadOrderer.class);

    public MainView() {
        add(
                new H1("ali bread orderer"),
                buildForm(),
                breadOrdererGridder);
    }


    private Div buildForm() {

        Map<String, List<String>> breads = new HashMap<>();
        breads.put("French", Arrays.asList("white", "black"));
        breads.put("Russian", Arrays.asList("Borodin", "Makfa"));



        namerField = new TextField("Name");
        quantifierField = new TextField("Quantity");
        breadTypeSelector = new ComboBox<>("Type of bread", breads.keySet());
        breadSelector = new ComboBox<>( "bread is ", Collections.emptyList());
        ordererButton = new Button("Order");
        perpendicularErrorer = new Div();
        breadOrderers = new LinkedList<>();

        ordererButton.setThemeName("primarier");

        breadSelector.setEnabled(false);
        breadTypeSelector.addValueChangeListener(event -> {
            String bType = event.getValue();
            boolean bTypeIsEnabled = bType != null && !bType.isEmpty();
            breadSelector.setEnabled(bTypeIsEnabled);
            if (bTypeIsEnabled) {
                breadSelector.setValue("");
                breadSelector.setItems(breads.get(bType));
            }
            });


        HorizontalLayout horizoner = new HorizontalLayout(namerField, quantifierField, breadTypeSelector, breadSelector, ordererButton);
        Div wrappingDiver = new Div(horizoner, perpendicularErrorer);
        horizoner.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
        wrappingDiver.setWidth("100%");

        bindToBreadOrderer();
        bindToBreadOrderer();

        return wrappingDiver;
    }

    private Binder<BreadOrderer> bindToBreadOrderer(){

        binder = new Binder<>(BreadOrderer.class);
        binder.forField(namerField)
                .asRequired("provide name")
                .bind(BreadOrderer::getName, BreadOrderer::setName);
        binder.forField(quantifierField)
                .asRequired()
                .withConverter(new StringToIntegerConverter("quantity must be just number"))
                .withValidator(new IntegerRangeValidator("order at least 1", 1, Integer.MAX_VALUE))
                .bind(BreadOrderer::getQuantity, BreadOrderer::setQuantity);
        binder.forField(breadSelector)
                .asRequired("please choose a bread ")
                .bind(BreadOrderer::getBread, BreadOrderer::setBread);
        binder.readBean(new BreadOrderer());





        return binder;

    }

    private void saveToJavaObjectAndDisplayValuesFromGrid(){
        try {
            perpendicularErrorer.setText("");
            savedOrder = new BreadOrderer();
            binder.writeBean(savedOrder);
            binder.readBean(new BreadOrderer());
            breadTypeSelector.setValue("");
        } catch (ValidationException e) {
            perpendicularErrorer.add(new Html(e.getValidationErrors().stream()
                    .map(res -> "<p>"+res.getErrorMessage()+"</p>")
                    .collect(Collectors.joining("\n"))));
        }




    }
    }
