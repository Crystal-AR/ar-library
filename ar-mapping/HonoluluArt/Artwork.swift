import Foundation
import MapKit
import Contacts

class Artwork: NSObject, MKAnnotation {
  let title: String?
  let locationName: String
  let discipline: String
  let coordinate: CLLocationCoordinate2D
  
  init(title: String, locationName: String, discipline: String, coordinate: CLLocationCoordinate2D) {
    self.title = title
    self.locationName = locationName
    self.discipline = discipline
    self.coordinate = coordinate
    
    super.init()
  }
  
  class func fromJSON(_ json: [JSONValue]) -> Artwork? {
    // 1
    var title: String
    if let titleOrNil = json[16].string {
      title = titleOrNil
    } else {
      title = ""
    }
    let locationName = json[12].string
    let discipline = json[15].string
    
    // 2
    let latitude = (json[18].string! as NSString).doubleValue
    let longitude = (json[19].string! as NSString).doubleValue
    let coordinate = CLLocationCoordinate2D(latitude: latitude, longitude: longitude)
    
    // 3
    return Artwork(title: title, locationName: locationName!, discipline: discipline!, coordinate: coordinate)
  }
  
  var subtitle: String? {
    return locationName
  }
  
  // MARK: - MapKit related methods
  
  // pinTintColor for disciplines: Sculpture, Plaque, Mural, Monument, other
  func pinTintColor() -> UIColor  {
    switch discipline {
    case "Sculpture", "Plaque":
      return MKPinAnnotationView.redPinColor()
    case "Mural", "Monument": 
      return MKPinAnnotationView.purplePinColor()
    default:
      return MKPinAnnotationView.greenPinColor()
    }
  }
  
  // annotation callout opens this mapItem in Maps app
  func mapItem() -> MKMapItem {
    let addressDict = [CNPostalAddressStreetKey: subtitle!]
    let placemark = MKPlacemark(coordinate: coordinate, addressDictionary: addressDict)
    
    let mapItem = MKMapItem(placemark: placemark)
    mapItem.name = title
    
    return mapItem
  }
  
}
