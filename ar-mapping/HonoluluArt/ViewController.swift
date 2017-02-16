import UIKit
import MapKit

class ViewController: UIViewController {

  @IBOutlet weak var mapView: MKMapView!
  
  let regionRadius: CLLocationDistance = 1000
  
  override func viewDidLoad() {
    super.viewDidLoad()
    
    // set initial location in Honolulu
    let initialLocation = CLLocation(latitude: 44.458298, longitude: -93.161604)
    centerMapOnLocation(initialLocation)
    
    loadInitialData()
    mapView.addAnnotations(artworks)
    
    mapView.delegate = self
    
    // show single artwork on map; comment out when loading PublicArt.json
//    let artwork = Artwork(title: "King David Kalakaua", locationName: "Waikiki Gateway Park",
//      discipline: "Sculpture", coordinate: CLLocationCoordinate2D(latitude: 21.283921,
//        longitude: -157.831661))
//    mapView.addAnnotation(artwork)
  }
  
  var artworks = [Artwork]()
  func loadInitialData() {
    // 1
    let fileName = Bundle.main.path(forResource: "PublicArt", ofType: "json");
    var data: Data?
    do {
      data = try Data(contentsOf: URL(fileURLWithPath: fileName!), options: NSData.ReadingOptions(rawValue: 0))
    } catch _ {
      data = nil
    }
    
    // 2
    var jsonObject: Any? = nil
    if let data = data {
      do {
       jsonObject = try JSONSerialization.jsonObject(with: data, options: JSONSerialization.ReadingOptions(rawValue: 0))
      } catch _ {
        jsonObject = nil
      }
    }
    
    // 3
    if let jsonObject = jsonObject as? [String: Any],
    // 4
    let jsonData = JSONValue.fromObject(jsonObject)?["data"]?.array {
      for artworkJSON in jsonData {
        if let artworkJSON = artworkJSON.array,
        // 5
        let artwork = Artwork.fromJSON(artworkJSON) {
          artworks.append(artwork)
        }
      }
    }
  }
  
  func centerMapOnLocation(_ location: CLLocation) {
    let coordinateRegion = MKCoordinateRegionMakeWithDistance(location.coordinate,
      regionRadius * 2.0, regionRadius * 2.0)
    mapView.setRegion(coordinateRegion, animated: true)
  }

}

