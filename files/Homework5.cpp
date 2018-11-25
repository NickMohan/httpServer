#include <iostream>
#include <string>

using namespace std;
//Node class used for constructing linked lists 
template <class T> class node {
public:
	T value;
	node<T> * next;
	node() { next = nullptr; }
	node(T t) { value = t; next = nullptr; }
};
//ThreeD class
template <class T> class ThreeD {
public:
	T ht;
	T wid;
	T dep;
	ThreeD(T i, T j, T k) { ht = i; wid = j; dep = k; }
	ThreeD() { ht = wid = dep = (T)0; }
	ThreeD(T i) { ht = i; wid = i; dep = i; }
	template <class T> friend ostream & operator<<(ostream & stream, const ThreeD<T> &t);
};
//Linked list class that connects nodes together to make list
template <class T> class linked_list {
public:
	int num_nodes;
	node<T> * head;
	linked_list() { num_nodes = 0; head = nullptr; }
	void push_front(T t);
	template <class T> friend ostream & operator<<(ostream & stream, linked_list<T> &t);
};
//Item class for bag. Pretty much a node like a linked list but has forward
//and backwards pointers with in the item and a value 
template <class X> class item {
public:
	X value;
	item<X> *next;
	item<X> *previous;
	item<X>(X v) { value = v; next = nullptr; previous = nullptr; }
	bool operator==(item<X> I) { return value == I.value; }
	//add more code if needed
};
//Main bag class. This is pretty much a two way linked list due to it is
//constructed out of items
template <class X> class bag {
public:
	item<X> *last;
	item<X> *first;
	int num_items;
	int size() { return num_items; }
	void show_bag();//Print all items in the bag
	bag() { last = nullptr; first = nullptr; num_items = 0; } //default constructor for bag class
	void push_back(X v);  //insert an item with value v to the back of the bag
	void push_front(X v); //insert an item with value v to the front of the bag
	void pop_back(); //delete the last item in the bag
	void pop_front(); //delete the first item in the bag
	void clear(); //Deletes everything
	X &operator[](int i); //Access bag item with index. 
	X front(); //returns the value of the first item
	X back(); // returns the value of the last item.
	template <class X> friend ostream & operator<<(ostream & stream, bag<X> &t);
};
//Returns the first item that is in the bag
template<class X> X bag<X>::front() {
	//cout << first->value;
	return first->value;
}
//Returns the last item that is in the bag
template<class X> X bag<X>::back() {
	//cout << last->value;
	return last->value;
}
//Prints out everyhting in the bag
template<class X> void bag<X>::show_bag() {
	item<X> * p = first;
	while (p->next != nullptr) {
		cout << p->value << " ";
		p = p->next;
	}
	cout << endl;
}
//Deletes everyhting in the bag
template<class X> void bag<X>::clear() {
	item<X> * p = first , * p1;
	while (p->next != nullptr) {
		p1 = p->next;
		delete p;
		p = p1;
	}
	num_items = 0;
}
//Overloads the [] operator so that the bag can be indexed
template<class X> X & bag<X>::operator[](int i) {
	item<X> * p = first;
	for (int z = 0; z < i; z++) {
		p = p->next;
	}
	return (p->value);
}
//adds a new value to the back of the bag
template <class X> void bag<X>::push_back(X v) {
	item<X> * p = new item<X>(v);
	if (num_items == 0) {
		last = p;
		first = p;
		num_items++;
	}
	else {
		p->previous = last;
		last = p;
		p->previous->next = last;
		num_items++;
	}
}
//adds a new value to the front of the bag
template <class X> void bag<X>::push_front(X v) {
	item<X> * p = new item<X>(v);
	if (num_items == 0) {
		first = p;
		last = p;
		num_items++;
	}
	else {
		p->next = first;
		first = p;
		p->next->previous = first;
		num_items++;
	}
}
//Removes the last item in the bag
template <class X> void bag<X>::pop_back() {
	item<X> * p = last->previous;
	delete last;
	last = p;
	last->next = nullptr;
	num_items--;
}
//Removes the first item in the bag
template <class X> void bag<X>::pop_front() {
	item<X> * p = first->next;
	delete first;
	first = p;
	first->previous = nullptr;
	num_items--;
}
//Overload the << operator so that it will print out all of the items in bag
template <class X> ostream & operator<<(ostream & stream, bag<X> &t) {
	item<X> * p = t.first;
	if (t.num_items == 0) {
		return (stream);
	}
	//stream << t.num_items<<" ";
	while (p != (t.last)->next) {
		stream << p->value << " ";
		p = p->next;
	}
	return stream;
}
//Overloads the << operator so that it can print out the ThreeD class
template <class T> ostream & operator<<(ostream & stream, const ThreeD<T> &t) {
	stream << "( " << t.ht << ", " << t.wid << ", " << t.dep << " )";
	return stream;
}
//Overloads the << operator to be able to print out a linked list
template <class T> ostream & operator<<(ostream & stream, linked_list<T> &t) {
	node<T> * p = t.head;
	while (p != nullptr) {
		stream << p->value << " ";
		p = p->next;
	}
	return stream;
}
//Adds a new value to the front of a linked list
template <class T> void linked_list<T>::push_front(T t) {
	node<T> * p = new node<T>(t);
	p->next = head;
	head = p;
}
//Main function for doing everything
int main() {
	bag<double> bag_d;
	bag_d.push_back(5.5);
	bag_d.push_back(6.6);
	bag_d.push_front(4.4);
	bag_d.push_front(3.3);
	bag_d.pop_front();
	bag_d.pop_back();
	cout << bag_d << endl;;

	bag<int> bag_i;
	bag_i.push_back(5);
	bag_i.push_back(6);
	bag_i.push_front(4);
	bag_i.push_front(3);
	bag_i.pop_front();
	bag_i.pop_back();
	cout << bag_i << endl;

	ThreeD<int> td3(3), td4(4), td5(5), td6(6), td7(100, 200, 300);
	bag<ThreeD<int>> bag_3D;
	bag_3D.push_back(td5);
	bag_3D.push_back(td6);
	bag_3D.push_front(td4);
	bag_3D.push_front(td3);
	bag_3D.pop_front();
	bag_3D.pop_back();
	cout << bag_3D << endl;;
	cout << bag_3D.front() << bag_3D.back();
	cout << bag_3D[0] << " " << bag_3D[1] << endl;
	bag_3D[1] = td7;
	cout << bag_3D[0] << " " << bag_3D[1] << endl;
	bag_3D.clear();
	cout << bag_3D << endl;;
	cout << bag_3D.size() << endl;


	linked_list<string>ls_1;
	ls_1.push_front("David");
	ls_1.push_front("John");
	ls_1.push_front("Pat");
	ls_1.push_front("Ben");
	ls_1.push_front("Jeff");
	cout << ls_1 << endl;

	linked_list<string>ls_2;
	ls_2.push_front("Wendy");
	ls_2.push_front("Mary");
	ls_2.push_front("Nancy");
	ls_2.push_front("Jennifer");
	cout << ls_2 << endl;

	bag<linked_list<string>> bag_string;

	bag_string.push_back(ls_1);

	bag_string.push_back(ls_2);

	cout << bag_string << endl;
	cout << bag_string[1] << endl;

	ThreeD<double> t10(3.2, 7.4, 8.9), t11(5.6, 7.7, 2.987), t12(4.6, 7.5, 3.1416), t13(55.6, 66.8, 333.45);
	linked_list<ThreeD<double>> LTD1;
	LTD1.push_front(t10);

	LTD1.push_front(t11);
	linked_list<ThreeD<double>> LTD2;
	LTD2.push_front(t13);
	LTD2.push_front(t12);
	LTD2.push_front(t10);
	LTD2.push_front(t11);

	bag<linked_list<ThreeD<double> > > BLTD;
	BLTD.push_back(LTD1);
	BLTD.push_back(LTD2);
	cout << BLTD << endl;

	getchar();
	getchar();
	return 0;

}
/*
//The following is a screenshot for the above main function
4.4 5.5
4 5
( 4, 4, 4 ) ( 5, 5, 5 )
( 4, 4, 4 )( 5, 5, 5 )( 4, 4, 4 ) ( 5, 5, 5 )
( 4, 4, 4 ) ( 100, 200, 300 )

0
Jeff Ben Pat John David
Jennifer Nancy Mary Wendy
Jeff Ben Pat John David  Jennifer Nancy Mary Wendy
Jennifer Nancy Mary Wendy
( 5.6, 7.7, 2.987 ) ( 3.2, 7.4, 8.9 )  ( 5.6, 7.7, 2.987 ) ( 3.2, 7.4, 8.9 ) ( 4.6, 7.5, 3.1416 ) ( 55.6, 66.8, 333.45 )
*/


