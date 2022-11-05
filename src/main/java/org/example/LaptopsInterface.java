package org.example;

//Odpowiednie importy adnotacji WebService
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import java.util.ArrayList;

@WebService
@SOAPBinding(style = Style.RPC)
public interface LaptopsInterface {
    @WebMethod int getManufacturerLaptopNumber(String manufacturer);
    @WebMethod int getResolutionLaptopNumber(String resolution);
    @WebMethod
    Laptop[] getLaptopListByFeatures(String f1, String f2, String f3, String f4, String f5);
    @WebMethod Laptop[] getAllLaptops();
}
